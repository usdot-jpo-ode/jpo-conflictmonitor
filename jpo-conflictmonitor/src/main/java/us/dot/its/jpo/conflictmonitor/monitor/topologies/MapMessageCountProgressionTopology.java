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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionStreamsAlgorithm;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.processors.MapMessageCountProgressionProcessor;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionConstants.DEFAULT_MAP_MESSAGE_COUNT_PROGRESSION_ALGORITHM;

@Component(DEFAULT_MAP_MESSAGE_COUNT_PROGRESSION_ALGORITHM)
@Slf4j
public class MapMessageCountProgressionTopology
        extends BaseStreamsTopology<MapMessageCountProgressionParameters>
        implements MapMessageCountProgressionStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    MapMessageCountProgressionAggregationStreamsAlgorithm aggregationAlgorithm;

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final String processedMapStateStore = parameters.getProcessedMapStateStoreName();
        final String latestMapStateStore = parameters.getLatestMapStateStoreName();
        final Duration retentionTime = Duration.ofMillis(parameters.getBufferTimeMs());

        builder.addStateStore(
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(processedMapStateStore, retentionTime),
                        JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.ProcessedMapGeoJson()
                )
        );

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(latestMapStateStore),
                        JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.ProcessedMapGeoJson()
                )
        );

        KStream<RsuIntersectionKey, ProcessedMap<LineString>> inputStream = builder.stream(parameters.getMapInputTopicName(), Consumed.with(JsonSerdes.RsuIntersectionKey(), JsonSerdes.ProcessedMapGeoJson()));

        var eventStream = inputStream
            .process(() -> new MapMessageCountProgressionProcessor(parameters), processedMapStateStore, latestMapStateStore);

        if (parameters.isAggregateEvents()) {
            // Aggregate events
            // Select new key that includes all the fields to aggregate on
            var aggKeyStream = eventStream.selectKey((key, value) -> {
                    var aggKey = new MapMessageCountProgressionAggregationKey();
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
                            us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapMessageCountProgressionAggregationKey(),
                            us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapMessageCountProgressionEvent())
                        .withStreamPartitioner(new IntersectionIdPartitioner<>()));
            // Plug in the aggregation topology
            aggregationAlgorithm.buildTopology(builder, aggKeyStream);
        } else {
            // Don't aggregate events, just send to the topic
            eventStream.to(parameters.getMapMessageCountProgressionEventOutputTopicName(),
                    Produced.with(
                            JsonSerdes.RsuIntersectionKey(),
                            us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapMessageCountProgressionEvent(),
                            new IntersectionIdPartitioner<>()));
        }
        return builder.build();
    }

    @Override
    public void setAggregationAlgorithm(MapMessageCountProgressionAggregationAlgorithm aggregationAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (aggregationAlgorithm instanceof MapMessageCountProgressionAggregationStreamsAlgorithm streamsAlgorithm) {
            this.aggregationAlgorithm = streamsAlgorithm;
        } else {
            throw new IllegalArgumentException("Aggregation algorithm must be a Streams algorithm");
        }
    }
}