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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.processors.BsmMessageCountProgressionProcessor;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionConstants.DEFAULT_BSM_MESSAGE_COUNT_PROGRESSION_ALGORITHM;

@Component(DEFAULT_BSM_MESSAGE_COUNT_PROGRESSION_ALGORITHM)
@Slf4j
public class BsmMessageCountProgressionTopology
        extends BaseStreamsTopology<BsmMessageCountProgressionParameters>
        implements BsmMessageCountProgressionStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    BsmMessageCountProgressionAggregationStreamsAlgorithm aggregationAlgorithm;

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final String processedBsmStateStore = parameters.getProcessedBsmStateStoreName();
        final String latestBsmStateStore = parameters.getLatestBsmStateStoreName();
        final Duration retentionTime = Duration.ofMillis(parameters.getBufferTimeMs());

        builder.addStateStore(
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(processedBsmStateStore, retentionTime),
                        us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(),
                        JsonSerdes.ProcessedBsm()
                )
        );

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(latestBsmStateStore),
                        us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(),
                        JsonSerdes.ProcessedBsm()
                )
        );
        KStream<BsmRsuIdKey, ProcessedBsm<Point>> inputStream = builder.stream(parameters.getBsmInputTopicName(),
                Consumed.with(
                        us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(),
                        JsonSerdes.ProcessedBsm()));

        // inputStream.print(Printed.toSysOut());

        var eventStream = inputStream
            .process(() -> new BsmMessageCountProgressionProcessor<>(parameters), processedBsmStateStore, latestBsmStateStore);

        if (parameters.isAggregateEvents()) {
            // Aggregate events
            // Select new key that includes all fields to aggregate on
            var aggKeyStream = eventStream.selectKey((key, value) -> {
                var aggKey = new BsmMessageCountProgressionAggregationKey();
                aggKey.setRsuId(key.getRsuId());
                aggKey.setBsmId(key.getBsmId());
                // TODO:
                //aggKey.setDataFrame(value.getDataFrame());
                //aggKey.setChange(value.getChange());
                return aggKey;
            })
            // Use RsuIdKey partitioner, which partitions only on the RSU ID, so the partitioning won't actually change
            .repartition(
                    Repartitioned.with(
                                us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmMessageCountProgressionAggregationKey(),
                                us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmMessageCountProgressionEvent())
                            .withStreamPartitioner(new RsuIdPartitioner<>()));
            // Plug in the aggregation algorithm
            aggregationAlgorithm.buildTopology(builder, aggKeyStream);
        } else {
            // Don't aggregate events
            eventStream.to(parameters.getBsmMessageCountProgressionEventOutputTopicName(),
                    Produced.with(
                            us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRsuIdKey(),
                            us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmMessageCountProgressionEvent()));
        }
        return builder.build();
    }

    @Override
    public void setAggregationAlgorithm(BsmMessageCountProgressionAggregationAlgorithm aggregationAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (aggregationAlgorithm instanceof BsmMessageCountProgressionAggregationStreamsAlgorithm streamsAlgorithm) {
            this.aggregationAlgorithm = streamsAlgorithm;
        } else {
            throw new IllegalArgumentException("Aggregation algorithm must be a streams algorithm");
        }
    }
}