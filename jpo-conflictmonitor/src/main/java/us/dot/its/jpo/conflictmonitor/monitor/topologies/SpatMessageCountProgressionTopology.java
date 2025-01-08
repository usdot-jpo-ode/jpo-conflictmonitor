package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Repartitioned;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionStreamsAlgorithm;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.processors.SpatMessageCountProgressionProcessor;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionConstants.DEFAULT_SPAT_MESSAGE_COUNT_PROGRESSION_ALGORITHM;

@Component(DEFAULT_SPAT_MESSAGE_COUNT_PROGRESSION_ALGORITHM)
@Slf4j
public class SpatMessageCountProgressionTopology
        extends BaseStreamsTopology<SpatMessageCountProgressionParameters>
        implements SpatMessageCountProgressionStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    SpatMessageCountProgressionAggregationStreamsAlgorithm aggregationAlgorithm;

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final String processedSpatStateStore = parameters.getProcessedSpatStateStoreName();
        final String latestSpatStateStore = parameters.getLatestSpatStateStoreName();
        final Duration retentionTime = Duration.ofMillis(parameters.getBufferTimeMs());

        builder.addStateStore(
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(processedSpatStateStore, retentionTime),
                        JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.ProcessedSpat()
                )
        );

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(latestSpatStateStore),
                        JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.ProcessedSpat()
                )
        );

        KStream<RsuIntersectionKey, ProcessedSpat> inputStream = builder.stream(parameters.getSpatInputTopicName(),
                Consumed.with(JsonSerdes.RsuIntersectionKey(), JsonSerdes.ProcessedSpat()));


        var eventStream = inputStream
            .process(() -> new SpatMessageCountProgressionProcessor(parameters), processedSpatStateStore, latestSpatStateStore);

        if (parameters.isAggregateEvents()) {
            // Aggregate events
            // Select new key that includes all fields to aggregate on
            var aggKeyStream = eventStream.selectKey((key, value) -> {
                var aggKey = new SpatMessageCountProgressionAggregationKey();
                aggKey.setRsuId(key.getRsuId());
                aggKey.setIntersectionId(key.getIntersectionId());
                aggKey.setRegion(key.getRegion());
                // TODO:
                //aggKey.setDataFrame(value.getDataFrame());
                //aggKey.setChange(value.getChange());
                return aggKey;
            })
            // Use same partitioner, IntersectionIdPartitioner, so that repartition on new key will
            // not actually change the partitions of any items
            .repartition(
                    Repartitioned.with(
                            us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatMessageCountProgressionAggregationKey(),
                            us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatMessageCountProgressionEvent())
                            .withStreamPartitioner(new IntersectionIdPartitioner<>()));
            // Plug in the aggregation topology
            aggregationAlgorithm.buildTopology(builder, aggKeyStream);
        } else {
            // Don't aggregate
            eventStream.to(parameters.getSpatMessageCountProgressionEventOutputTopicName(),
                    Produced.with(JsonSerdes.RsuIntersectionKey(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatMessageCountProgressionEvent()));
        }
        return builder.build();
    }

    @Override
    public void setAggregationAlgorithm(SpatMessageCountProgressionAggregationAlgorithm aggregationAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (aggregationAlgorithm instanceof SpatMessageCountProgressionAggregationStreamsAlgorithm streamsAlgorithm) {
            this.aggregationAlgorithm = streamsAlgorithm;
        } else {
            throw new IllegalArgumentException("Aggregation algorithm must be a Streams algorithm");
        }
    }
}