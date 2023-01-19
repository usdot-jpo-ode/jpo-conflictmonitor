package us.dot.its.jpo.conflictmonitor.monitor.topologies.broadcast_rate;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_requirement.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;


@Component(DEFAULT_MAP_BROADCAST_RATE_ALGORITHM)
public class MapBroadcastRateTopology 
    implements MapBroadcastRateStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MapBroadcastRateTopology.class);

    MapBroadcastRateParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(MapBroadcastRateParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public MapBroadcastRateParameters getParameters() {
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
        logger.info("Starting MapBroadcastRateTopology.");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        streams.start();
        logger.info("Started MapBroadcastRateTopology.");
    }

    private Topology buildTopology() {
        var builder = new StreamsBuilder();

        KStream<RsuIntersectionKey, ProcessedMap> processedMapStream = builder
            .stream(parameters.getInputTopicName(), 
                Consumed.with(
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMap())
                        .withTimestampExtractor(new MapTimestampExtractor())
            );

        // Extract validation info for Minimum Data events
        processedMapStream
            .peek((key, value) -> {
                logger.info("Min data received MAP {}\n{}", key, value);
            })
            // Filter out messages that are valid
            .filter((key, value) -> value.getProperties() != null && !value.getProperties().getCti4501Conformant())
            .peek((key, value) -> {
                logger.info("Filtered MAP {}", key);
            })
            // Produce events for the messages that have validation errors
            .map((key, value) -> {
                List<String> validationMessages = value.getProperties()
                    .getValidationMessages()
                    .stream()
                    .map(valMsg -> String.format("%s (%s)", valMsg.getMessage(), valMsg.getSchemaPath()))
                    .collect(Collectors.toList());
                var minDataEvent = new MapMinimumDataEvent();
                minDataEvent.setMissingDataElements(validationMessages);
                minDataEvent.setIntersectionId(key.getIntersectionId());
                minDataEvent.setSourceDeviceId(key.getRsuId());

                // Get the time window this event would be in without actually performing windowing
                // we just need to add the window timestamps to the event.

                // Use a tumbling window with no grace to avoid duplicates
                var timeWindows = TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(parameters.getRollingPeriodSeconds()));

                // Gets a map of all time windows this instant could be in 
                Map<Long, TimeWindow> windows = timeWindows.windowsFor(MapTimestampExtractor.extractTimestamp(value));

                // Pick one (random map entry, but there should only be one for the tumbling window)
                TimeWindow window = windows.values().stream().findAny().orElse(null);                
                if (window != null) {
                    var timePeriod = new ProcessingTimePeriod();
                    timePeriod.setBeginTimestamp(window.startTime().atZone(ZoneOffset.UTC));
                    timePeriod.setEndTimestamp(window.endTime().atZone(ZoneOffset.UTC));
                    minDataEvent.setTimePeriod(timePeriod);
                }

                return KeyValue.pair(key, minDataEvent);

            }).to(parameters.getMinimumDataEventTopicName(),
                Produced.with(
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                    JsonSerdes.MapMinimumDataEvent(), 
                    new RsuIdPartitioner<RsuIntersectionKey, MapMinimumDataEvent>())
            );

        // Perform count for Broadcast Rate analysis
        KStream<Windowed<RsuIntersectionKey>, Long> countStream = 
            processedMapStream
            .mapValues((oldValue) -> 1) // Map the value to the constant int 1 (key remains the same)
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

        if (parameters.isDebug()) {
            countStream = countStream.peek((windowedKey, value) -> {
                logger.info("Map Count {} {}", windowedKey, value);
            });
        }

        KStream<RsuIntersectionKey, MapBroadcastRateEvent> eventStream = countStream            
            .filter((windowedKey, value) -> {
                if (value != null) {
                    long counts = value.longValue();
                    return (counts < parameters.getLowerBound() || counts > parameters.getUpperBound());
                }
                return false;
            })
            .map((windowedKey, counts) -> {
                // Generate an event
                MapBroadcastRateEvent event = new MapBroadcastRateEvent();
                event.setSourceDeviceId(windowedKey.key().getRsuId());
                event.setTopicName(parameters.getInputTopicName());
                ProcessingTimePeriod timePeriod = new ProcessingTimePeriod();
                
                // Grab the timestamps from the time window
                timePeriod.setBeginTimestamp(windowedKey.window().startTime().atZone(ZoneOffset.UTC));
                timePeriod.setEndTimestamp(windowedKey.window().endTime().atZone(ZoneOffset.UTC));
                event.setTimePeriod(timePeriod);
                event.setNumberOfMessages(counts != null ? counts.intValue() : -1);

                // Change the windowed key back to a normal key
                return KeyValue.pair(windowedKey.key(), event);
            });

        if (parameters.isDebug()) {
            eventStream = eventStream.peek((key, event) -> {
                logger.info("MAP Broadcast Rate {}, {}", key, event);
            });
        }

        eventStream.to(parameters.getOutputEventTopicName(),
            Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(), 
                JsonSerdes.MapBroadcastRateEvent(), 
                new RsuIdPartitioner<RsuIntersectionKey, MapBroadcastRateEvent>())
        );
        
        return builder.build();
    }



    

    @Override
    public void stop() {
        logger.info("Stopping MapBroadcastRateTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped MapBroadcastRateTopology.");
    }

   
    
    
}
