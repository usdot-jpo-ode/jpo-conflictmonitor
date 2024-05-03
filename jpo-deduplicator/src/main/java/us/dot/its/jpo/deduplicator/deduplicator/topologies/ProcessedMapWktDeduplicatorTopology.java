package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.deduplicator.deduplicator.models.ProcessedMapWktPair;
import us.dot.its.jpo.deduplicator.deduplicator.serialization.PairSerdes;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;

import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class ProcessedMapWktDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(MapDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    String inputTopic;
    String outputTopic;
    Properties streamsProperties;
    ObjectMapper objectMapper;

    public ProcessedMapWktDeduplicatorTopology(String inputTopic, String outputTopic, Properties streamsProperties){
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
        streams.start();
    }

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, ProcessedMap<String>> inputStream = builder.stream(inputTopic, Consumed.with(Serdes.String(), JsonSerdes.ProcessedMapWKT()));

        inputStream.print(Printed.toSysOut());

        KStream<String, ProcessedMap<String>> deduplicatedStream = inputStream
            .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.ProcessedMapWKT()))
            .aggregate(() -> new ProcessedMapWktPair(new ProcessedMap<String>(), true),
            (key, newValue, aggregate)->{

                // Handle the first message where the aggregate map isn't good.
                if(aggregate.getMessage().getProperties() == null){
                    return new ProcessedMapWktPair(newValue, true );
                }

                Instant newValueTime = newValue.getProperties().getTimeStamp().toInstant();
                Instant oldValueTime = aggregate.getMessage().getProperties().getTimeStamp().toInstant();
                
                if(newValueTime.minus(Duration.ofHours(1)).isAfter(oldValueTime)){
                    return new ProcessedMapWktPair(newValue, true );
                }else{
                    ZonedDateTime newValueTimestamp = newValue.getProperties().getTimeStamp();
                    ZonedDateTime newValueOdeReceivedAt = newValue.getProperties().getOdeReceivedAt();

                    newValue.getProperties().setTimeStamp(aggregate.getMessage().getProperties().getTimeStamp());
                    newValue.getProperties().setOdeReceivedAt(aggregate.getMessage().getProperties().getOdeReceivedAt());

                    int oldHash = aggregate.getMessage().getProperties().hashCode();
                    int newhash = newValue.getProperties().hashCode();

                    if(oldHash != newhash){
                        newValue.getProperties().setTimeStamp(newValueTimestamp);
                        newValue.getProperties().setOdeReceivedAt(newValueOdeReceivedAt);
                        return new ProcessedMapWktPair(newValue, true);
                    }else{
                        return new ProcessedMapWktPair(aggregate.getMessage(), false);
                    }
                }
            }, Materialized.with(Serdes.String(), PairSerdes.ProcessedMapWktPair()))
            .toStream()
            .flatMap((key, value) ->{
                ArrayList<KeyValue<String, ProcessedMap<String>>> outputList = new ArrayList<>();
                if(value != null && value.isShouldSend()){
                    outputList.add(new KeyValue<>(key, value.getMessage()));   
                }
                return outputList;
            });

        
        deduplicatedStream.to(outputTopic, Produced.with(Serdes.String(), JsonSerdes.ProcessedMapWKT()));

        return builder.build();

    }

    public void stop() {
        logger.info("Stopping Processed Map deduplicator Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Processed Map deduplicator Socket Broadcast Topology.");
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
