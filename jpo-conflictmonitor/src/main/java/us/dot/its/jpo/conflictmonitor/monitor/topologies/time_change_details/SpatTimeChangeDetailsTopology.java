package us.dot.its.jpo.conflictmonitor.monitor.topologies.time_change_details;



import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.TimeChangeDetailsConstants.*;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.stereotype.Component;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.processors.SpatSequenceProcessorSupplier;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

@Component(DEFAULT_SPAT_TIME_CHANGE_DETAILS_ALGORITHM)
public class SpatTimeChangeDetailsTopology implements SpatTimeChangeDetailsStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SpatTimeChangeDetailsTopology.class);

    SpatTimeChangeDetailsParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(SpatTimeChangeDetailsParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public SpatTimeChangeDetailsParameters getParameters() {
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
        logger.info("Starting SpatTimeChangeDetailsTopology.");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        streams.setUncaughtExceptionHandler(ex -> {
            logger.error("KafkaStreams uncaught exception, will try replacing thread", ex);
            return StreamThreadExceptionResponse.REPLACE_THREAD;
        });
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
        logger.info("Started SpatTimeChangeDetailsTopology.");


        //Topology topology = BsmEventTopology.build(conflictMonitorProps.getKafkaTopicOdeBsmJson(), conflictMonitorProps.getKafkaTopicCmBsmEvent());
        // KafkaStreams streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("bsmEvent"));
        // Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        // streams.start(); 
    }

    private Topology buildTopology() {
        Topology builder = new Topology();

        final String SPAT_SOURCE = "Spat Message Source";
        final String SPAT_SEQUENCE_PROCESSOR = "Spat Sequencer Processor";
        final String SPAT_TIME_CHANGE_DETAIL_SINK = "Spat Time Change Detail Sink";


        builder.addSource(SPAT_SOURCE, Serdes.String().deserializer(), us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat().deserializer(), this.parameters.getSpatInputTopicName());
        builder.addProcessor(SPAT_SEQUENCE_PROCESSOR, new SpatSequenceProcessorSupplier(this.parameters), SPAT_SOURCE);
        

 
        StoreBuilder<KeyValueStore<String, SpatTimeChangeDetailAggregator>> storeBuilder = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(parameters.getSpatTimeChangeDetailsStateStoreName()),
            Serdes.String(),
            JsonSerdes.SpatTimeChangeDetailAggregator()
        );


        builder.addStateStore(storeBuilder, SPAT_SEQUENCE_PROCESSOR);

        builder.addSink(SPAT_TIME_CHANGE_DETAIL_SINK, this.parameters.getSpatTimeChangeDetailsTopicName(), Serdes.String().serializer(), JsonSerdes.TimeChangeDetailsEvent().serializer(), SPAT_SEQUENCE_PROCESSOR);
        
        
        return builder;
    }



    

    @Override
    public void stop() {
        logger.info("Stopping SpatBroadcastRateTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped SpatBroadcastRateTopology.");
    }

   
    
}
