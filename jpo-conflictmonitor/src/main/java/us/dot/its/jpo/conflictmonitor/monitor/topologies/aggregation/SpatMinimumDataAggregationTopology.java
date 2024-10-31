package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.SpatMinimumDataAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation.EventAggregationProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.time.Duration;


@Slf4j
public class SpatMinimumDataAggregationTopology
    extends BaseStreamsBuilder<AggregationParameters>
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
    public Class<RsuIntersectionKey> keyClass() {
        return RsuIntersectionKey.class;
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
        final String eventName = constructEventAggregation().getEventType();
        final String storeName = eventName + "Store";
        final var eventTopicMap = parameters.getEventTopicMap();
        String eventAggregationTopic;
        if (eventTopicMap.containsKey(eventName)) {
            eventAggregationTopic = parameters.getEventTopicMap().get(eventName);
        } else {
            throw new RuntimeException(String.format("Aggregation topic for %s not found in aggregation.eventTopicMap", eventName));
        }


        final var storeBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(storeName),
                        keySerde(),
                        eventAggregationSerde()
                );
        builder.addStateStore(storeBuilder);

        var produced = IntersectionKey.class.isAssignableFrom(keyClass())
                ? Produced.with(    // Use intersection id partitioner if we can
                    keySerde(),
                    eventAggregationSerde(),
                    new IntersectionIdPartitioner<>())
                : Produced.with(    // We don't know how to partition the key, use default partitioner
                        keySerde(),
                        eventAggregationSerde());

        inputStream.process(() -> new EventAggregationProcessor<RsuIntersectionKey, SpatMinimumDataEvent, SpatMinimumDataEventAggregation>(


                ))
                    .to(eventAggregationTopic, produced);

    }




}
