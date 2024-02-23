package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.deduplicator.deduplicator.models.OdeMapPair;
import us.dot.its.jpo.deduplicator.deduplicator.serialization.PairSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;
import us.dot.its.jpo.ode.model.OdeMapData;
import us.dot.its.jpo.ode.model.OdeMapMetadata;
import us.dot.its.jpo.ode.model.OdeMapPayload;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionReferenceID;

import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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


    public MapDeduplicatorTopology(String inputTopic, String outputTopic, Properties streamsProperties){
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
        this.streamsProperties = streamsProperties;
        this.objectMapper = new ObjectMapper();
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

        KStream<Void, OdeMapData> inputStream = builder.stream(inputTopic, Consumed.with(Serdes.Void(), JsonSerdes.OdeMap()));


        KStream<String, OdeMapData> mapRekeyedStream = inputStream.selectKey((key, value)->{
                J2735IntersectionReferenceID intersectionId = ((OdeMapPayload)value.getPayload()).getMap().getIntersections().getIntersections().get(0).getId();
                RsuIntersectionKey newKey = new RsuIntersectionKey();
                newKey.setRsuId(((OdeMapMetadata)value.getMetadata()).getOriginIp());
                newKey.setIntersectionReferenceID(intersectionId);
                return newKey.toString();
        });

        KStream<String, OdeMapData> deduplicatedStream = mapRekeyedStream
            .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.OdeMap()))
            .aggregate(() -> new OdeMapPair(new OdeMapData(), true),
            (key, newValue, aggregate)->{
                
                if(aggregate.getMessage().getMetadata() == null){
                    return new OdeMapPair(newValue, true);
                }

                Instant newValueTime = getInstantFromMap(newValue);
                Instant oldValueTime = getInstantFromMap(aggregate.getMessage());

                if(newValueTime.minus(Duration.ofHours(1)).isAfter(oldValueTime)){
                    return new OdeMapPair(newValue, true );
                    
                }else{

                    OdeMapPayload oldPayload = (OdeMapPayload)aggregate.getMessage().getPayload();
                    OdeMapPayload newPayload = (OdeMapPayload)newValue.getPayload();

                    Integer oldTimestamp = oldPayload.getMap().getTimeStamp();
                    Integer newTimestamp = newPayload.getMap().getTimeStamp();
                    

                    newPayload.getMap().setTimeStamp(oldTimestamp);

                    int oldHash = hashMapMessage(aggregate.getMessage());
                    int newhash = hashMapMessage(newValue);

                    if(oldHash != newhash){
                        newPayload.getMap().setTimeStamp(newTimestamp);
                        return new OdeMapPair(newValue, true);
                    }else{
                        return new OdeMapPair(aggregate.getMessage(), false);
                    }
                }
            }, Materialized.with(Serdes.String(), PairSerdes.OdeMapPair()))
            .toStream()
            .flatMap((key, value) ->{
                ArrayList<KeyValue<String, OdeMapData>> outputList = new ArrayList<>();
                if(value != null && value.isShouldSend()){
                    outputList.add(new KeyValue<>(key, value.getMessage()));   
                }
                return outputList;
            });
        
        deduplicatedStream.to(outputTopic, Produced.with(Serdes.String(), JsonSerdes.OdeMap()));

        return builder.build();

    }

    public void stop() {
        logger.info("Stopping Map deduplicator Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Map deduplicator Socket Broadcast Topology.");
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
