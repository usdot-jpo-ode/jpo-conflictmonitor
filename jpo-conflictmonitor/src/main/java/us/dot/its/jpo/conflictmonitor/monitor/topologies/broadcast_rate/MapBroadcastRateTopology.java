package us.dot.its.jpo.conflictmonitor.monitor.topologies.broadcast_rate;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;

import java.time.Duration;
import java.util.Properties;



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
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateStreamsAlgorithm;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.conflictmonitor.monitor.models.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;


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

        KStream<Windowed<String>, Long> countStream = builder
            .stream(parameters.getInputTopicName(), 
                Consumed.with(
                            Serdes.String(), 
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.OdeMap())
                        .withTimestampExtractor(new MapTimestampExtractor())
            )
            .map((oldKey, oldValue) -> {
                // Change key to RSU IP address and value to constant = 1
                var metadata = (OdeMapMetadata)oldValue.getMetadata();
                var newKey = metadata.getOriginIp();
                return KeyValue.pair(newKey, 1);
            })
            .groupByKey(
                Grouped.with(Serdes.String(), Serdes.Integer())
            )
            .windowedBy(
                // Hopping window
                TimeWindows
                    .of(Duration.ofSeconds(parameters.getRollingPeriodSeconds()))
                    .advanceBy(Duration.ofSeconds(parameters.getOutputIntervalSeconds()))
                    .grace(Duration.ofMillis(parameters.getGracePeriodMilliseconds()))
            )
            .count(
                Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("map-counts")
                    .withKeySerde(Serdes.String())
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

        KStream<String, MapBroadcastRateEvent> eventStream = countStream            
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
                event.setSourceDeviceId(windowedKey.key());
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
                logger.info("MAP Broadcast Rate {}, {}", key, event);
            });
        }

        eventStream.to(parameters.getOutputEventTopicName(),
            Produced.with(
                Serdes.String(), 
                JsonSerdes.MapBroadcastRateEvent())
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
