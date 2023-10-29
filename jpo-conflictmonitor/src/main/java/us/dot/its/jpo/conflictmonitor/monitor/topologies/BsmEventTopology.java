package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseTopologyBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEventIntersectionKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.processors.BsmEventProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventConstants.DEFAULT_BSM_EVENT_ALGORITHM;

@Component(DEFAULT_BSM_EVENT_ALGORITHM)
public class BsmEventTopology
        extends BaseTopologyBuilder<BsmEventParameters>
        implements BsmEventStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(BsmEventTopology.class);

    public final static String BSM_SOURCE = "BSM_Event_Source";
    final String BSM_PROCESSOR = "BSM_Event_Processor";
    final String BSM_SINK = "BSM_Event_Sink";

    
    // Tracks when a new stream of BSMS arrives through the system. Once the stream of BSM's ends, emits an event
    // containing the start and end BSM's in the chain.
    public Topology buildTopology(Topology bsmEventBuilder) {


        // BSM_SOURCE is created in MessageIngestTopology


        bsmEventBuilder.addProcessor(BSM_PROCESSOR,
                () -> {
                        var processor = new BsmEventProcessor();
                        processor.setPunctuationType(punctuationType);
                        processor.setMapIndex(mapIndex);
                        processor.setSimplifyPath(parameters.isSimplifyPath());
                        processor.setSimplifyPathToleranceMeters(parameters.getSimplifyPathToleranceMeters());
                        return processor;
                    },
                BSM_SOURCE);



        bsmEventBuilder.addSink(
                BSM_SINK,
                parameters.getOutputTopic(),
                JsonSerdes.BsmEventIntersectionKey().serializer(),
                JsonSerdes.BsmEvent().serializer(),
                new RsuIdPartitioner<BsmEventIntersectionKey, BsmEvent>(),
                BSM_PROCESSOR);

        StoreBuilder<TimestampedKeyValueStore<BsmEventIntersectionKey, BsmEvent>> storeBuilder
                = Stores.timestampedKeyValueStoreBuilder(
                    Stores.persistentTimestampedKeyValueStore(parameters.getStateStoreName()),
                    JsonSerdes.BsmEventIntersectionKey(),
                    JsonSerdes.BsmEvent()
                );


        bsmEventBuilder.addStateStore(storeBuilder, BSM_PROCESSOR);

        return bsmEventBuilder;
    }







    @Override
    protected Logger getLogger() {
        return logger;
    }



    @Setter
    @Getter
    public PunctuationType punctuationType = PunctuationType.WALL_CLOCK_TIME;


    private MapIndex mapIndex;



    @Override
    public void setMapIndex(MapIndex mapIndex) {
        this.mapIndex = mapIndex;
    }


    @Override
    public void validate() {
        if (mapIndex == null) {
            throw new IllegalArgumentException("MapIndex is not set");
        }
    }

}
