package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionStreamsAlgorithm;
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

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final String processedMapStateStore = parameters.getProcessedMapStateStoreName();
        final String latestMapStateStore = parameters.getLatestMapStateStoreName();
        final Duration retentionTime = Duration.ofMillis(parameters.getBufferTimeMs());

        builder.addStateStore(
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(processedMapStateStore, retentionTime),
                        Serdes.String(),
                        JsonSerdes.ProcessedMapGeoJson()
                )
        );

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(latestMapStateStore),
                        Serdes.String(),
                        JsonSerdes.ProcessedMapGeoJson()
                )
        );

        KStream<String, ProcessedMap<LineString>> inputStream = builder.stream(parameters.getMapInputTopicName(), Consumed.with(Serdes.String(), JsonSerdes.ProcessedMapGeoJson()));

        inputStream
            .process(() -> new MapMessageCountProgressionProcessor(parameters), processedMapStateStore, latestMapStateStore)
            .to(parameters.getMapMessageCountProgressionEventOutputTopicName(), Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.MapMessageCountProgressionEvent()));

        return builder.build();
    }
}