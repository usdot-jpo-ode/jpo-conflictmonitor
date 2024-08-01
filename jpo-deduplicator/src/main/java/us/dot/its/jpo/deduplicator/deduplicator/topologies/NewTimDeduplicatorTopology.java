package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.deduplicator.deduplicator.serialization.JsonSerdes;

import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers.OdeTimJsonProcessorSupplier;

public class NewTimDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(TimDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    String inputTopic;
    String outputTopic;
    String stateStoreName;
    Properties streamsProperties;
    ObjectMapper objectMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    public NewTimDeduplicatorTopology(String inputTopic, String outputTopic, String stateStoreName,
            Properties streamsProperties) {
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
        this.stateStoreName = stateStoreName;
        this.streamsProperties = streamsProperties;
        this.objectMapper = new ObjectMapper();
    }

    public void start() {
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        if (exceptionHandler != null)
            streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null)
            streams.setStateListener(stateListener);
        logger.info("Starting Tim Deduplicator Topology");
        streams.start();
    }

    public JsonNode genJsonNode() {
        return objectMapper.createObjectNode();
    }

    

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<Void, JsonNode> inputStream = builder.stream(inputTopic,
                Consumed.with(Serdes.Void(), JsonSerdes.JSON()));

        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(stateStoreName),
                Serdes.String(), JsonSerdes.JSON()));

        

        KStream<String, JsonNode> timRekeyedStream = inputStream.selectKey((key, value) -> {
            try {
                
                JsonNode travellerInformation = value.get("payload")
                        .get("data")
                        .get("MessageFrame")
                        .get("value")
                        .get("TravelerInformation");

                String rsuIP = value.get("metadata").get("originIp").asText();
                String packetId = travellerInformation.get("packetID").asText();
                String msgCnt = travellerInformation.get("msgCnt").asText();

                String newKey = rsuIP + "_" + packetId + "_" + msgCnt;
                return newKey;
            } catch (Exception e) {
                return "";
            }
        });

        // final KStream<byte[], String> deduplicated = timRekeyedStream.transform(
        // // In this example, we assume that the record value as-is represents a unique
        // event ID by
        // // which we can perform de-duplication. If your records are different, adapt
        // the extractor
        // // function as needed.
        // () -> new DeduplicationTransformer<>(windowSize.toMillis(), (key, value) ->
        // value),
        // storeName);

        // final Topology topology = new Topology();
        // topology.addSource(
        //         "source-node",
        //         Serdes.String().deserializer(),
        //         JsonSerdes.JSON().deserializer(),
        //         inputTopic);

        // topology.addProcessor(stateStoreName, new OdeTimJsonProcessorSupplier(),"source-node");

        // topology.addSink(
        //         "sink-node",
        //         outputTopic,
        //         Serdes.String().serializer(),
        //         Serdes.Double().serializer(),
        //         "aggregate-price");

        KStream<String, JsonNode> processedStream = timRekeyedStream.process(new OdeTimJsonProcessorSupplier(stateStoreName), stateStoreName);

        // processedStream.print(Printed.toSysOut());

        // KStream<String, JsonNode> deduplicatedStream = timRekeyedStream
        // .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.JSON()))
        // .aggregate(
        // ()-> new JsonPair(genJsonNode(), true),
        // (aggKey, newValue, aggregate) ->{

        // // Filter out results that cannot be properly key tested
        // if(aggKey.equals("")){
        // return new JsonPair(newValue, false);
        // }

        // if(aggregate.getMessage().get("metadata") == null){
        // return new JsonPair(newValue, true);
        // }

        // Instant oldValueTime = getInstantFromJsonTim(aggregate.getMessage());
        // Instant newValueTime = getInstantFromJsonTim(newValue);

        // if(newValueTime.minus(Duration.ofHours(1)).isAfter(oldValueTime)){
        // return new JsonPair(newValue, true );
        // }else{
        // return new JsonPair(aggregate.getMessage(), false);
        // }
        // },
        // Materialized.with(Serdes.String(), PairSerdes.JsonPair())
        // )
        // .toStream()
        // .flatMap((key, value) ->{
        // ArrayList<KeyValue<String, JsonNode>> outputList = new ArrayList<>();
        // if(value != null && value.isShouldSend()){
        // outputList.add(new KeyValue<>(key, value.getMessage()));

        // }
        // return outputList;
        // });

        // deduplicatedStream.to(outputTopic, Produced.with(Serdes.String(),
        // JsonSerdes.JSON()));

        return builder.build();

    }

    public void stop() {
        logger.info("Stopping Tim deduplicator Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Tim deduplicator Socket Broadcast Topology.");
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
