package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;

import us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers.OdeRawEncodedTimProcessorSupplier;

public class OdeRawEncodedTimDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(OdeRawEncodedTimDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    String inputTopic;
    String outputTopic;
    Properties streamsProperties;
    ObjectMapper objectMapper;
    DeduplicatorProperties props;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    public OdeRawEncodedTimDeduplicatorTopology(DeduplicatorProperties props, Properties streamsProperties){
        this.streamsProperties = streamsProperties;
        this.objectMapper = DateJsonMapper.getInstance();
        this.props = props;
    }

    
    public void start() {
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        logger.info("Starting Tim Deduplicator Topology");
        streams.start();
    }

    public JsonNode genJsonNode(){
        return objectMapper.createObjectNode();
    }

    public Instant getInstantFromJsonTim(JsonNode tim){
        try{
            String time = tim.get("metadata").get("odeReceivedAt").asText();
            return Instant.from(formatter.parse(time));
        }catch(Exception e){
            System.out.println("Failed to parse time");
            return Instant.ofEpochMilli(0);
        }
    }


    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<Void, JsonNode> inputStream = builder.stream(props.getKafkaTopicOdeRawEncodedTimJson(), Consumed.with(Serdes.Void(), JsonSerdes.JSON()));

        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(props.getKafkaStateStoreOdeRawEncodedTimJsonName()),
                Serdes.String(), JsonSerdes.JSON()));

        KStream<String, JsonNode> timRekeyedStream = inputStream.selectKey((key, value)->{
            try{

                String messageBytes = value.get("payload")
                    .get("data")
                    .get("bytes").asText();

                int hash = Objects.hash(messageBytes);

                String rsuIP = value.get("metadata").get("originIp").asText();

                String newKey = rsuIP + "_" + hash;
                return newKey;
            }catch(Exception e){
                return "";
            }
        }).repartition(Repartitioned.with(Serdes.String(), JsonSerdes.JSON()));

        

        KStream<String, JsonNode> deduplicatedStream = timRekeyedStream.process(new OdeRawEncodedTimProcessorSupplier(props), props.getKafkaStateStoreOdeRawEncodedTimJsonName());

        deduplicatedStream.to(props.getKafkaTopicDeduplicatedOdeRawEncodedTimJson(), Produced.with(Serdes.String(), JsonSerdes.JSON()));

        return builder.build();

    }

    public void stop() {
        logger.info("Stopping OdeRawEncodedTim Deduplicator Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped OdeRawEncodedTim Deduplicator Socket Broadcast Topology.");
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
