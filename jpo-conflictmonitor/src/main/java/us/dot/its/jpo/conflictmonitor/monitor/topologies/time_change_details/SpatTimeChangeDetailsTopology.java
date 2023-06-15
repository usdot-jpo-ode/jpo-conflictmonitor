package us.dot.its.jpo.conflictmonitor.monitor.topologies.time_change_details;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.processors.SpatSequenceProcessorSupplier;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.TimeChangeDetailsConstants.DEFAULT_SPAT_TIME_CHANGE_DETAILS_ALGORITHM;

@Component(DEFAULT_SPAT_TIME_CHANGE_DETAILS_ALGORITHM)
public class SpatTimeChangeDetailsTopology
        extends BaseStreamsTopology<SpatTimeChangeDetailsParameters>
        implements SpatTimeChangeDetailsStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SpatTimeChangeDetailsTopology.class);
    @Override
    protected Logger getLogger() {
        return logger;
    }



    @Override
    public Topology buildTopology() {
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



    



}