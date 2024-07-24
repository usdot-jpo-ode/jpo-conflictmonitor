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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.ValidationConstants.DEFAULT_MAP_VALIDATION_ALGORITHM;


/**
 * Assessments/validations for MAP messages.
 * <p>Reads {@link ProcessedMap} messages.
 * <p>Produces {@link MapBroadcastRateEvent}s and {@link MapMinimumDataEvent}s
 */
@Component(DEFAULT_MAP_VALIDATION_ALGORITHM)
public class MapValidationTopology 
    extends BaseValidationTopology<MapValidationParameters>
    implements MapValidationStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MapValidationTopology.class);
    @Override
    protected Logger getLogger() {
        return logger;
    }

    private static final String LATEST_TIMESTAMP_STORE = "latest-timestamp-store";

    MapTimestampDeltaStreamsAlgorithm timestampDeltaAlgorithm;

    @Override
    public MapTimestampDeltaAlgorithm getTimestampDeltaAlgorithm() {
        return timestampDeltaAlgorithm;
    }

    @Override
    public void setTimestampDeltaAlgorithm(MapTimestampDeltaAlgorithm timestampDeltaAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (timestampDeltaAlgorithm instanceof MapTimestampDeltaStreamsAlgorithm timestampDeltaStreamsAlgorithm) {
            this.timestampDeltaAlgorithm = timestampDeltaStreamsAlgorithm;
        } else {
            throw new IllegalArgumentException("algorithm is not an instance of MapTimestampDeltaStreamsAlgorithm");
        }
    }

    @Override
    protected void validate() {
        super.validate();

        if (timestampDeltaAlgorithm == null) {
            throw new IllegalStateException("MapTimestampDeltaAlgorithm is not set.");
        }
    }

    public Topology buildTopology() {

        var builder = new StreamsBuilder();

        // Create state store for zero count
        var zeroCountStoreBuilder =
                Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(LATEST_TIMESTAMP_STORE),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        Serdes.Long());

        builder.addStateStore(zeroCountStoreBuilder);

        KStream<RsuIntersectionKey, ProcessedMap<LineString>> processedMapStream = builder
            .stream(parameters.getInputTopicName(), 
                Consumed.with(
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMapGeoJson())
                        .withTimestampExtractor(new TimestampExtractorForBroadcastRate())
            );

        // timestamp delta plugin after reading processed MAPs
        timestampDeltaAlgorithm.buildTopology(processedMapStream);

        // Extract validation info for Minimum Data events
        KStream<RsuIntersectionKey, MapMinimumDataEvent> minDataStream = processedMapStream
            // Filter out messages that are valid
            .filter((key, value) -> value.getProperties() != null && !value.getProperties().getCti4501Conformant())
           
            // Produce events for the messages that have validation errors
            .map((key, value) -> {
                var minDataEvent = new MapMinimumDataEvent();
                var valMsgList = value.getProperties().getValidationMessages();
                var timestamp = TimestampExtractorForBroadcastRate.extractTimestamp(value);
                populateMinDataEvent(key, minDataEvent, valMsgList, parameters.getRollingPeriodSeconds(), 
                    timestamp);
                return KeyValue.pair(key, minDataEvent);
            });

        
        minDataStream = minDataStream.peek((key, value) -> {
            var intersectionKey = IntersectionRegion.fromRsuIntersectionKey(key);
            if (parameters.getDebug(intersectionKey)) {
                logger.info("MAP Min Data Event for intersection {}", intersectionKey);
            }
        });
       
            
        minDataStream.to(parameters.getMinimumDataTopicName(),
            Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                JsonSerdes.MapMinimumDataEvent(), 
                new IntersectionIdPartitioner<RsuIntersectionKey, MapMinimumDataEvent>())
        );




        // Save the timestamp of the latest message for each key in a state store to be queried by the zero-check task
        processedMapStream.process(() ->
                        new MapZeroRateChecker(
                            parameters.getRollingPeriodSeconds(),
                            parameters.getOutputIntervalSeconds(),
                            parameters.getInputTopicName(),
                            LATEST_TIMESTAMP_STORE
                ), LATEST_TIMESTAMP_STORE)
                // Emit zero-rate events to the topic
                .to(parameters.getBroadcastRateTopicName(),
                        Produced.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                JsonSerdes.MapBroadcastRateEvent(),
                                new IntersectionIdPartitioner<RsuIntersectionKey, MapBroadcastRateEvent>())
                );


        // Perform count for Broadcast Rate analysis
        KStream<Windowed<RsuIntersectionKey>, Long> countStream = 
            processedMapStream
                .mapValues((value) -> 1) // Map the value to the constant int 1 (key remains the same)
                .groupByKey(
                    Grouped.with(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), Serdes.Integer())
                )
                .windowedBy(
                    // Hopping window
                    TimeWindows
                        .ofSizeAndGrace(Duration.ofSeconds(parameters.getRollingPeriodSeconds()), Duration.ofMillis(parameters.getGracePeriodMilliseconds()))
                        .advanceBy(Duration.ofSeconds(parameters.getOutputIntervalSeconds()))
                )
                .count(
                    Materialized.<RsuIntersectionKey, Long, WindowStore<Bytes, byte[]>>as("map-counts")
                        .withKeySerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey())
                        .withValueSerde(Serdes.Long())
                )
                .suppress(
                     Suppressed.untilWindowCloses(BufferConfig.unbounded())
                )
                .toStream();


        countStream = countStream.peek((windowedKey, value) -> {
            var intersectionKey = IntersectionRegion.fromRsuIntersectionKey(windowedKey.key());
            if (parameters.getDebug(intersectionKey)) {
                logger.info("Map Count {} {}", windowedKey, value);
            }
        });
        

        KStream<RsuIntersectionKey, MapBroadcastRateEvent> eventStream = countStream            
            .filter((windowedKey, value) -> {
                if (value != null) {
                    long counts = value.longValue();
                    var intersectionKey = IntersectionRegion.fromRsuIntersectionKey(windowedKey.key());
                    return (counts < parameters.getLowerBound(intersectionKey) || counts > parameters.getUpperBound(intersectionKey));
                }
                return false;
            })
            .map((windowedKey, counts) -> {
                // Generate an event
                MapBroadcastRateEvent event = new MapBroadcastRateEvent();
                event.setSource(windowedKey.key().toString());
                event.setIntersectionID(windowedKey.key().getIntersectionId());
                event.setRoadRegulatorID(windowedKey.key().getRegion());
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

        
        eventStream = eventStream.peek((key, event) -> {
            var intersectionKey = IntersectionRegion.fromRsuIntersectionKey(key);
            if (parameters.getDebug(intersectionKey)) {
                logger.info("MAP Broadcast Rate {}, {}", key, event);
            }
        });
        

        eventStream.to(parameters.getBroadcastRateTopicName(),
            Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                JsonSerdes.MapBroadcastRateEvent(), 
                new IntersectionIdPartitioner<RsuIntersectionKey, MapBroadcastRateEvent>())
        );
        
        return builder.build();
    }



}
