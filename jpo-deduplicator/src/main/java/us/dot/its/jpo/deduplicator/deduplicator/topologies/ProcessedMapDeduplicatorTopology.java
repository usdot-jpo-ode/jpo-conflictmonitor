package us.dot.its.jpo.deduplicator.deduplicator.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.processors.suppliers.ProcessedMapProcessorSupplier;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Properties;

public class ProcessedMapDeduplicatorTopology {

    private static final Logger logger = LoggerFactory.getLogger(MapDeduplicatorTopology.class);

    Topology topology;
    KafkaStreams streams;
    Properties streamsProperties;
    ObjectMapper objectMapper;
    DeduplicatorProperties props;

    public ProcessedMapDeduplicatorTopology(DeduplicatorProperties props, Properties streamsProperties){
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
        logger.info("Starting Processed Map Deduplicator Topology");
        streams.start();
    }

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, ProcessedMap<LineString>> inputStream = builder.stream(props.getKafkaTopicProcessedMap(), Consumed.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()));

        builder.addStateStore(Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(props.getKafkaStateStoreProcessedMapName()),
                Serdes.String(), JsonSerdes.ProcessedMapGeoJson()));
        
        KStream<String, ProcessedMap<LineString>> deduplicatedStream = inputStream.process(new ProcessedMapProcessorSupplier(props), props.getKafkaStateStoreProcessedMapName());
        
        deduplicatedStream.to(props.getKafkaTopicDeduplicatedProcessedMap(), Produced.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()));

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
