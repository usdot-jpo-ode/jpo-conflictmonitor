package us.dot.its.jpo.conflictmonitor.monitor.topologies.broadcast_rate;

import java.time.Duration;
import java.time.ZonedDateTime;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map.MapBroadcastRateParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.broadcast_rate.MapTimestampExtractor;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeMapPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735MAP;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;


@Component(DEFAULT_MAP_BROADCAST_RATE_ALGORITHM)
public class MapBroadcastRateTopology implements MapBroadcastRateAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(MapBroadcastRateTopology.class);

    MapBroadcastRateParameters parameters;

    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(MapBroadcastRateParameters parameters) {
        this.parameters = parameters;
    }

   

    @Override
    public void start() {
        if (parameters == null) {
            throw new IllegalStateException("Start called before setting parameters.");
        }
        if (parameters.getStreamsProperties() == null) {
            throw new IllegalStateException("Streams properties are not set.");
        }
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        
        Topology topology = buildTopology();
        Properties streamsProperties = parameters.getStreamsProperties();
        streams = new KafkaStreams(topology, streamsProperties);
        streams.start();
    }

    private Topology buildTopology() {
        var builder = new StreamsBuilder();

        builder
            .stream(parameters.getInputTopicName(), 
                Consumed.with(
                            Serdes.String(), 
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.OdeMap())
                        //.withTimestampExtractor(new MapTimestampExtractor())
            )
            .peek((key, value) -> {
                logger.info("Received MAP");
            })
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
            .toStream()
            .map(
                (windowedKey, value) -> KeyValue.pair(windowedKey.key(), value)
            )
            .peek((key, value) -> {
                logger.info("Map Count {} {}", key, value);
            })
            .to(parameters.getOutputCountTopicName(),
                Produced.with(
                    Serdes.String(), 
                    Serdes.Long())
            );
        
        
        return builder.build();
    }



    

    @Override
    public void stop() {
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
    }

   
    
    
}
