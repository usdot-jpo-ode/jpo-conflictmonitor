
package us.dot.its.jpo.deduplication.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.deduplication.deduplicator.models.JsonPair;
import us.dot.its.jpo.deduplication.deduplicator.serialization.PairSerdes;
import us.dot.its.jpo.deduplication.deduplicator.serialization.JsonSerdes;

import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

public class TimDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(MapDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    String inputTopic;
    String outputTopic;
    Properties streamsProperties;
    ObjectMapper objectMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    public TimDeduplicatorTopology(String inputTopic, String outputTopic, Properties streamsProperties){
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
    }

    public JsonNode genJsonNode(){
        return objectMapper.createObjectNode();
    }

    public Instant getInstantFromJsonTim(JsonNode tim){

        // Iterator fields = tim.fields();
        // while(fields.hasNext()){
        //     System.out.println(fields.next());
        // }

        // System.out.println(tim.get("metadata").toPrettyString());
        try{
            String time = tim.get("metadata").get("odeReceivedAt").asText();
            return Instant.from(formatter.parse(time));
        }catch(Exception e){
            System.out.println("Failed to parse time");
            return Instant.ofEpochMilli(0);
        }
        
        // return Instant.now();
    }


    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<Void, JsonNode> inputStream = builder.stream(inputTopic, Consumed.with(Serdes.Void(), JsonSerdes.JSON()));

        KStream<String, JsonNode> timRekeyedStream = inputStream.selectKey((key, value)->{
            try{
                String packetID = value.get("payload")
                    .get("data")
                    .get("AdvisorySituationData")
                    .get("asdmDetails")
                    .get("advisoryMessage")
                    .get("Ieee1609Dot2Data")
                    .get("content")
                    .get("unsecuredData")
                    .get("MessageFrame")
                    .get("value")
                    .get("TravelerInformation")
                    .get("packetID")
                    .asText();
                return packetID;
            }catch(Exception e){
                return "";
            }
        });

        KStream<String, JsonNode> deduplicatedStream = timRekeyedStream
            .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.JSON()))
            .aggregate(
                    ()-> new JsonPair(genJsonNode(), true),
                    (aggKey, newValue, aggregate) ->{

                        if(aggregate.getMessage().get("metadata") == null){
                            return new JsonPair(newValue, true);
                        }

                        Instant oldValueTime = getInstantFromJsonTim(aggregate.getMessage());
                        Instant newValueTime = getInstantFromJsonTim(newValue);

                        if(newValueTime.minus(Duration.ofHours(1)).isAfter(oldValueTime)){
                            return new JsonPair(newValue, true );
                        }else{
                            return new JsonPair(aggregate.getMessage(), false);
                        }
                    },
                    Materialized.with(Serdes.String(), PairSerdes.JsonPair())
            )
            .toStream()
            .flatMap((key, value) ->{
                ArrayList<KeyValue<String, JsonNode>> outputList = new ArrayList<>();
                if(value != null && value.isShouldSend()){
                    outputList.add(new KeyValue<>(key, value.getMessage()));   
                }
                return outputList;
            });

        deduplicatedStream.to(outputTopic, Produced.with(Serdes.String(), JsonSerdes.JSON()));

        return builder.build();

    }

    public void stop() {
        logger.info("Stopping Map Deduplication Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Map Deduplication Socket Broadcast Topology.");
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
