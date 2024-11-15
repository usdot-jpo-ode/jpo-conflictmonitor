package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers.OdeMapJsonProcessorSupplier;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeMapPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionReferenceID;

import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;

public class MapDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(MapDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    String inputTopic;
    String outputTopic;
    Properties streamsProperties;
    ObjectMapper objectMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    DeduplicatorProperties props;


    public MapDeduplicatorTopology(DeduplicatorProperties props, Properties streamsProperties){
        this.props = props;
        this.streamsProperties = streamsProperties;
        this.objectMapper = DateJsonMapper.getInstance();
    }


    
    public void start() {
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        logger.info("Starting Map Deduplicator Topology");
        streams.start();
    }

    public Instant getInstantFromMap(OdeMapData map){
        String time = ((OdeMapMetadata)map.getMetadata()).getOdeReceivedAt();

        return Instant.from(formatter.parse(time));
    }

    public int hashMapMessage(OdeMapData map){
        OdeMapPayload payload = (OdeMapPayload)map.getPayload();
        return Objects.hash(payload.toJson());
        
    }

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<Void, OdeMapData> inputStream = builder.stream(props.getKafkaTopicOdeMapJson(), Consumed.with(Serdes.Void(), JsonSerdes.OdeMap()));

        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(props.getKafkaStateStoreOdeMapJsonName()),
                Serdes.String(), JsonSerdes.OdeMap()));

        KStream<String, OdeMapData> mapRekeyedStream = inputStream.selectKey((key, value)->{
                J2735IntersectionReferenceID intersectionId = ((OdeMapPayload)value.getPayload()).getMap().getIntersections().getIntersections().get(0).getId();
                RsuIntersectionKey newKey = new RsuIntersectionKey();
                newKey.setRsuId(((OdeMapMetadata)value.getMetadata()).getOriginIp());
                newKey.setIntersectionReferenceID(intersectionId);
                return newKey.toString();
        }).repartition(Repartitioned.with(Serdes.String(), JsonSerdes.OdeMap()));

        KStream<String, OdeMapData> deduplicatedStream = mapRekeyedStream.process(new OdeMapJsonProcessorSupplier(props), props.getKafkaStateStoreOdeMapJsonName());
        
        deduplicatedStream.to(props.getKafkaTopicDeduplicatedOdeMapJson(), Produced.with(Serdes.String(), JsonSerdes.OdeMap()));

        return builder.build();

    }

    public void stop() {
        logger.info("Stopping Map Deduplicator Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Map Deduplicator Socket Broadcast Topology.");
    }

    StateListener stateListener;
    public void registerStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    StreamsUncaughtExceptionHandler exceptionHandler;
    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
