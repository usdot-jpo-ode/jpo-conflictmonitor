package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers.ProcessedSpatProcessorSupplier;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;

import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class ProcessedSpatDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(ProcessedSpatDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    String inputTopic;
    String outputTopic;
    Properties streamsProperties;
    ObjectMapper objectMapper;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    DeduplicatorProperties props;


    public ProcessedSpatDeduplicatorTopology(DeduplicatorProperties props, Properties streamsProperties){
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

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, ProcessedSpat> inputStream = builder.stream(props.getKafkaTopicProcessedSpat(), Consumed.with(Serdes.String(), JsonSerdes.ProcessedSpat()));

        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(props.getKafkaStateStoreProcessedSpatName()),
                Serdes.String(), JsonSerdes.ProcessedSpat()));
        
        KStream<String, ProcessedSpat> deduplicatedStream = inputStream.process(new ProcessedSpatProcessorSupplier(props), props.getKafkaStateStoreProcessedSpatName());
        
        deduplicatedStream.print(Printed.toSysOut());

        deduplicatedStream.to(props.getKafkaTopicDeduplicatedProcessedSpat(), Produced.with(Serdes.String(), JsonSerdes.ProcessedSpat()));

        return builder.build();

    }

    public void stop() {
        logger.info("Stopping Processed SPaT deduplicator Socket Broadcast Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped Processed SPaT deduplicator Socket Broadcast Topology.");
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
