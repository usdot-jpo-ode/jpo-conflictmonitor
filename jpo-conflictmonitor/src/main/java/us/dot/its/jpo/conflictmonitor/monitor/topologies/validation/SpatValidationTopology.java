package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat.SpatMinimumDataAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat.SpatMinimumDataAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.ValidationConstants.DEFAULT_SPAT_VALIDATION_ALGORITHM;

/**
 * Assessments/validations for SPAT messages.
 * <p>Reads {@link ProcessedSpat} messages.
 * <p>Produces {@link SpatBroadcastRateEvent}s and {@link SpatMinimumDataEvent}s
 */
@Component(DEFAULT_SPAT_VALIDATION_ALGORITHM)
public class SpatValidationTopology
        extends BaseValidationTopology<SpatValidationParameters>
        implements SpatValidationStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SpatValidationTopology.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }

    private static final String LATEST_TIMESTAMP_STORE = "latest-timestamp-store";

    SpatTimestampDeltaStreamsAlgorithm timestampDeltaAlgorithm;
    SpatMinimumDataAggregationStreamsAlgorithm minimumDataAggregationAlgorithm;

    @Override
    public SpatTimestampDeltaAlgorithm getTimestampDeltaAlgorithm() {
        return timestampDeltaAlgorithm;
    }

    @Override
    public void setTimestampDeltaAlgorithm(SpatTimestampDeltaAlgorithm timestampDeltaAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (timestampDeltaAlgorithm instanceof SpatTimestampDeltaStreamsAlgorithm timestampDeltaStreamsAlgorithm) {
            this.timestampDeltaAlgorithm = timestampDeltaStreamsAlgorithm;
        } else {
            throw new IllegalArgumentException("Algorithm is not an instance of SpatTimestampDeltaStreamsAlgorithm");
        }
    }

    @Override
    public void setMinimumDataAggregationAlgorithm(SpatMinimumDataAggregationAlgorithm minimumDataAggregationAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (minimumDataAggregationAlgorithm instanceof SpatMinimumDataAggregationStreamsAlgorithm minimumDataAggregationStreamsAlgorithm) {
            this.minimumDataAggregationAlgorithm = minimumDataAggregationStreamsAlgorithm;
        } else {
            throw new IllegalArgumentException("Algorithm is not an instance of SpatMinimumDataAggregationStreamsAlgorithm");
        }
    }

    @Override
    protected void validate() {
        super.validate();

        if (timestampDeltaAlgorithm == null) {
            throw new IllegalStateException("SpatTimestampDeltaAlgorithm is not set");
        }
    }

    @Override
    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        // Create state store for zero count
        var zeroCountStoreBuilder =
                Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(LATEST_TIMESTAMP_STORE),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        Serdes.Long());

        builder.addStateStore(zeroCountStoreBuilder);

        KStream<RsuIntersectionKey, ProcessedSpat> processedSpatStream = builder
                .stream(parameters.getInputTopicName(),
                        Consumed.with(
                                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
                                .withTimestampExtractor(new TimestampExtractorForBroadcastRate())
                );

        // timestamp delta plugin after reading processed SPATs
        timestampDeltaAlgorithm.buildTopology(builder, processedSpatStream);


        // Extract validation info for Minimum Data events
        var minimumDataEventStream = processedSpatStream
                .filter((key, value) -> value != null && !value.isCti4501Conformant())
                .map((key, value) -> {
                    var minDataEvent = new SpatMinimumDataEvent();
                    var valMsgList = value.getValidationMessages();
                    var timestamp = TimestampExtractorForBroadcastRate.extractTimestamp(value);
                    populateMinDataEvent(key, minDataEvent, valMsgList, parameters.getRollingPeriodSeconds(),
                            timestamp);

                    return KeyValue.pair(key, minDataEvent);
                })
                .peek((key, value) -> {
                    if (parameters.isDebug()) {
                        logger.info("SpatMinimumDataEvent {}", key);
                    }
                });


        // If aggregation is enabled, don't send individual events to the topic
        // This is a read-only flag, so the subtopology for the unchosen option is not constructed at all
        if (parameters.isAggregateMinimumDataEvents()) {
            // Aggregate
            minimumDataAggregationAlgorithm.buildTopology(builder, minimumDataEventStream);
        } else {
            // Dont' aggregate
            minimumDataEventStream
                    .to(parameters.getMinimumDataTopicName(),
                            Produced.with(
                                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                    JsonSerdes.SpatMinimumDataEvent(),
                                    new IntersectionIdPartitioner<>())
                    );
        }


        // Save the timestamp of the latest message for each key in a state store to be queried by the zero-check task
        processedSpatStream.process(() ->
                        new SpatZeroRateChecker(
                                parameters.getRollingPeriodSeconds(),
                                parameters.getOutputIntervalSeconds(),
                                parameters.getInputTopicName(),
                                LATEST_TIMESTAMP_STORE
                        ), LATEST_TIMESTAMP_STORE)
                .to(parameters.getBroadcastRateTopicName(),
                        Produced.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                JsonSerdes.SpatBroadcastRateEvent(),
                                new IntersectionIdPartitioner<RsuIntersectionKey, SpatBroadcastRateEvent>()
                        ));

        // Perform count for Broadcast Rate analysis
        KStream<Windowed<RsuIntersectionKey>, Long> countStream =
                processedSpatStream
                        .mapValues((value) -> 1)    // Map the value to the constant int 1 (key remains the same)
                        .groupByKey(
                                Grouped.with(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), Serdes.Integer())
                        )
                        .windowedBy(
                                // Hopping window
                                TimeWindows
                                        .ofSizeAndGrace(
                                                Duration.ofSeconds(parameters.getRollingPeriodSeconds()),
                                                Duration.ofMillis(parameters.getGracePeriodMilliseconds()))
                                        .advanceBy(Duration.ofSeconds(parameters.getOutputIntervalSeconds()))
                        )
                        .count(
                                Materialized.<RsuIntersectionKey, Long, WindowStore<Bytes, byte[]>>as("spat-counts")
                                        .withKeySerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey())
                                        .withValueSerde(Serdes.Long())
                        )
                        .suppress(
                                Suppressed.untilWindowCloses(BufferConfig.unbounded())
                        )
                        .toStream();

        if (parameters.isDebug()) {
            countStream = countStream.peek((windowedKey, value) -> {
                logger.info("SPAT Count {} {}", windowedKey, value);
            });
        }

        KStream<RsuIntersectionKey, SpatBroadcastRateEvent> eventStream = countStream
                .filter((windowedKey, value) -> {
                    if (value != null) {
                        long counts = value.longValue();
                        return (counts < parameters.getLowerBound() || counts > parameters.getUpperBound());
                    }
                    return false;
                })
                .map((windowedKey, counts) -> {
                    // Generate an event
                    SpatBroadcastRateEvent event = new SpatBroadcastRateEvent();
                    event.setSource(windowedKey.key().toString());
                    event.setIntersectionID(windowedKey.key().getIntersectionId());
                    event.setRoadRegulatorID(-1);
                    event.setTopicName(parameters.getInputTopicName());
                    ProcessingTimePeriod timePeriod = new ProcessingTimePeriod();

                    // Grab the timestamps from the time window
                    timePeriod.setBeginTimestamp(windowedKey.window().startTime().toEpochMilli());
                    timePeriod.setEndTimestamp(windowedKey.window().endTime().toEpochMilli());
                    event.setTimePeriod(timePeriod);
                    event.setNumberOfMessages(counts != null ? counts.intValue() : -1);

                    // Change the windowed key back to a normal key
                    return KeyValue.pair(windowedKey.key(), event);
                });

        if (parameters.isDebug()) {
            eventStream = eventStream.peek((key, event) -> {
                logger.info("SPAT Broadcast Rate {}, {}", key, event);
            });
        }

        eventStream.to(parameters.getBroadcastRateTopicName(),
                Produced.with(
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.SpatBroadcastRateEvent(),
                        new IntersectionIdPartitioner<RsuIntersectionKey, SpatBroadcastRateEvent>())
        );

        return builder.build();
    }


}
