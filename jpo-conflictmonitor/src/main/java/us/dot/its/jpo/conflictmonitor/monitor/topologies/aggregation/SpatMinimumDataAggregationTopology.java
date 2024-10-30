package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.CommonAggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.SpatMinimumDataAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation.EventAggregationProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;


@Slf4j
public class SpatMinimumDataAggregationTopology
    extends BaseStreamsBuilder<CommonAggregationParameters>
    implements SpatMinimumDataAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }



    @Override
    public SpatMinimumDataEventAggregation constructEventAggregation() {
        return new SpatMinimumDataEventAggregation();
    }

    @Override
    public Serde<RsuIntersectionKey> keySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    public Serde<SpatMinimumDataEvent> eventSerde() {
        return JsonSerdes.SpatMinimumDataEvent();
    }

    @Override
    public Serde<SpatMinimumDataEventAggregation> eventAggregationSerde() {
        return JsonSerdes.SpatMinimumDataEventAggregation();
    }

    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, SpatMinimumDataEvent> inputStream) {

        // Name store by convention
        final String storeName = constructEventAggregation().getEventType() + "Store";

        final var storeBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(storeName),
                        keySerde(),
                        eventAggregationSerde()
                );
        builder.addStateStore(storeBuilder);

//        var aggEventStream =
//            inputStream
//                    .process(() -> new EventAggregationProcessor<RsuIntersectionKey, SpatMinimumDataEvent, SpatMinimumDataEventAggregation>())
//                    .to()

    }



}
