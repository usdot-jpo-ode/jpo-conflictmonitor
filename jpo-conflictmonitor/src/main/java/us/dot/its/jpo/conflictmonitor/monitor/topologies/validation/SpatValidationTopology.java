package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.stereotype.Component;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.ValidationConstants.*;

import java.time.Duration;
import java.time.ZoneOffset;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * Assessments/validations for SPAT messages.
 * <p>Reads {@link ProcessedSpat} messages.
 * <p>Produces {@link SpatBroadcastRateEvent}s and {@link SpatMinimumDataEvent}s
 */
@Component(DEFAULT_SPAT_VALIDATION_ALGORITHM)
public class SpatValidationTopology 
    extends BaseValidationTopology
    implements SpatValidationStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SpatValidationTopology.class);

    SpatValidationParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(SpatValidationParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public SpatValidationParameters getParameters() {
        return parameters;
    }

    @Override
    public void setStreamsProperties(Properties streamsProperties) {
       this.streamsProperties = streamsProperties;
    }

    @Override
    public Properties getStreamsProperties() {
        return streamsProperties;
    }

    @Override
    public KafkaStreams getStreams() {
        return streams;
    }
   

    @Override
    public void start() {
        if (parameters == null) {
            throw new IllegalStateException("Start called before setting parameters.");
        }
        if (streamsProperties == null) {
            throw new IllegalStateException("Streams properties are not set.");
        }
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        logger.info("Starting SpatBroadcastRateTopology.");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        streams.setUncaughtExceptionHandler(ex -> {
            logger.error("KafkaStreams uncaught exception, will try replacing thread", ex);
            return StreamThreadExceptionResponse.REPLACE_THREAD;
        });
        streams.start();
        logger.info("Started SpatBroadcastRateTopology.");
    }

    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        KStream<RsuIntersectionKey, ProcessedSpat> processedSpatStream = builder
            .stream(parameters.getInputTopicName(), 
                Consumed.with(
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat())
                        .withTimestampExtractor(new TimestampExtractorForBroadcastRate())
                );

        // Extract validation info for Minimum Data events
        processedSpatStream
            .filter((key, value) -> !value.getCti4501Conformant())
            .map((key, value) -> {
                var minDataEvent = new SpatMinimumDataEvent();
                var valMsgList = value.getValidationMessages();
                var timestamp = TimestampExtractorForBroadcastRate.extractTimestamp(value);
                populateMinDataEvent(key, minDataEvent, valMsgList, parameters.getRollingPeriodSeconds(), 
                    timestamp);
                
                return KeyValue.pair(key, minDataEvent);
            })
            .peek((key, value) -> {
                if (parameters.isDebug()){
                    logger.info("SpatMinimumDataEvent {}", key);
                }
            })
            .to(parameters.getMinimumDataTopicName(),
                Produced.with(
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                    JsonSerdes.SpatMinimumDataEvent(),
                    new RsuIdPartitioner<RsuIntersectionKey, SpatMinimumDataEvent>())
            );
        
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
                event.setSourceDeviceId(windowedKey.key().getRsuId());
                event.setIntersectionId(windowedKey.key().getIntersectionId());
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
                new RsuIdPartitioner<RsuIntersectionKey, SpatBroadcastRateEvent>())
        );
        
        return builder.build();
    }



    

    @Override
    public void stop() {
        logger.info("Stopping SpatBroadcastRateTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped SpatBroadcastRateTopology.");
    }

   
    
}
