package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventStreamsAlgorithm;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventConstants.*;

import java.util.Properties;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.processors.BsmEventProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

@Component(DEFAULT_BSM_EVENT_ALGORITHM)
public class BsmEventTopology implements BsmEventStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(BsmEventTopology.class);
    
    // Tracks when a new stream of BSMS arrives through the system. Once the stream of BSM's ends, emits an event containing the start and end BSM's in the chain.
    private Topology build() {
        Topology bsmEventBuilder = new Topology();

        final String BSM_SOURCE = "BSM Event Source";
        final String BSM_PROCESSOR = "BSM Event Processor";
        final String BSM_SINK = "BSM Event Sink";


        bsmEventBuilder.addSource(BSM_SOURCE, Serdes.String().deserializer(), JsonSerdes.OdeBsm().deserializer(), parameters.getInputTopic());
        //bsmEventBuilder.addSource(AutoOffsetReset.LATEST, BSM_SOURCE, new BsmTimestampExtractor(), Serdes.String().deserializer(), JsonSerdes.OdeBsm().deserializer(), inputTopic);
        bsmEventBuilder.addProcessor(BSM_PROCESSOR, BsmEventProcessor::new, BSM_SOURCE);
        bsmEventBuilder.addSink(BSM_SINK, parameters.getOutputTopic(), Serdes.String().serializer(), JsonSerdes.BsmEvent().serializer(), BSM_PROCESSOR);

        StoreBuilder<TimestampedKeyValueStore<String, BsmEvent>> storeBuilder = Stores.timestampedKeyValueStoreBuilder(
            Stores.persistentTimestampedKeyValueStore(parameters.getStateStoreName()),
            Serdes.String(),
            JsonSerdes.BsmEvent()
        );


        bsmEventBuilder.addStateStore(storeBuilder, BSM_PROCESSOR);

        return bsmEventBuilder;
    }

    BsmEventParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;
    StateListener stateListener;
    StreamsUncaughtExceptionHandler exceptionHandler;

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
        logger.info("Starting BsmEvent Topology.");
        Topology topology = build();
        streams = new KafkaStreams(topology, streamsProperties);
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        streams.start();
        logger.info("Started BsmEvent Topology");
        
    }

    @Override
    public void stop() {
        logger.info("Stopping BsmEvent Topology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped BsmEvent Topology.");
    }

    @Override
    public void setParameters(BsmEventParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public BsmEventParameters getParameters() {
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
    public void registerStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    @Override
    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
