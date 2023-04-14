package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
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




    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        KStream<RsuIntersectionKey, ProcessedMap> processedMapStream = builder
            .stream(parameters.getInputTopicName(), 
                Consumed.with(
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMap())
                        .withTimestampExtractor(new TimestampExtractorForBroadcastRate())
            );

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
            var rsuId = key.getRsuId();
            if (rsuId != null && parameters.getDebug(rsuId)) {
                logger.info("MAP Min Data Event {}", key);
            }
        });
       
            
        minDataStream.to(parameters.getMinimumDataTopicName(),
            Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                JsonSerdes.MapMinimumDataEvent(), 
                new RsuIdPartitioner<RsuIntersectionKey, MapMinimumDataEvent>())
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
            var rsuId = windowedKey.key().getRsuId();
            if (rsuId != null && parameters.getDebug(rsuId)) {
                logger.info("Map Count {} {}", windowedKey, value);
            }
        });
        

        KStream<RsuIntersectionKey, MapBroadcastRateEvent> eventStream = countStream            
            .filter((windowedKey, value) -> {
                if (value != null) {
                    long counts = value.longValue();
                    String rsuId = windowedKey.key().getRsuId();
                    return (counts < parameters.getLowerBound(rsuId) || counts > parameters.getUpperBound(rsuId));
                }
                return false;
            })
            .map((windowedKey, counts) -> {
                // Generate an event
                MapBroadcastRateEvent event = new MapBroadcastRateEvent();
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

        
        eventStream = eventStream.peek((key, event) -> {
            var rsuId = key.getRsuId();
            if (rsuId != null && parameters.getDebug(rsuId)) {
                logger.info("MAP Broadcast Rate {}, {}", key, event);
            }
        });
        

        eventStream.to(parameters.getBroadcastRateTopicName(),
            Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                JsonSerdes.MapBroadcastRateEvent(), 
                new RsuIdPartitioner<RsuIntersectionKey, MapBroadcastRateEvent>())
        );
        
        return builder.build();
    }


    
}
