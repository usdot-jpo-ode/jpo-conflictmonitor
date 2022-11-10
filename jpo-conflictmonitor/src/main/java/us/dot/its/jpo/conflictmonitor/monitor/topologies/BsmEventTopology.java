package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.processors.BsmEventProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

public class BsmEventTopology {
    
    // Tracks when a new stream of BSMS arrives through the system. Once the stream of BSM's ends, emits an event containing the start and end BSM's in the chain.
    public static Topology build(String inputTopic, String outputTopic) {
        Topology bsmEventBuilder = new Topology();

        final String BSM_SOURCE = "BSM Event Source";
        final String BSM_PROCESSOR = "BSM Event Processor";
        final String BSM_SINK = "BSM Event Sink";


        bsmEventBuilder.addSource(BSM_SOURCE, Serdes.String().deserializer(), JsonSerdes.OdeBsm().deserializer(), inputTopic);
        bsmEventBuilder.addProcessor(BSM_PROCESSOR, BsmEventProcessor::new, BSM_SOURCE);
        bsmEventBuilder.addSink(BSM_SINK, outputTopic, Serdes.String().serializer(), JsonSerdes.BsmEvent().serializer(), BSM_PROCESSOR);

        StoreBuilder<TimestampedKeyValueStore<String, BsmEvent>> storeBuilder = Stores.timestampedKeyValueStoreBuilder(
            Stores.persistentTimestampedKeyValueStore("bsm-event-state-store"),
            Serdes.String(),
            JsonSerdes.BsmEvent()
        );


        bsmEventBuilder.addStateStore(storeBuilder, BSM_PROCESSOR);

        return bsmEventBuilder;
    }
}
