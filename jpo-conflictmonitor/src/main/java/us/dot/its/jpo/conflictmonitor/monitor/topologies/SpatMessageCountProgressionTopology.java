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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionStreamsAlgorithm;
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

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final String processedSpatStateStore = parameters.getProcessedSpatStateStoreName();
        final String latestSpatStateStore = parameters.getLatestSpatStateStoreName();
        final Duration retentionTime = Duration.ofMillis(parameters.getBufferTimeMs());

        builder.addStateStore(
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(processedSpatStateStore, retentionTime),
                        Serdes.String(),
                        JsonSerdes.ProcessedSpat()
                )
        );

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(latestSpatStateStore),
                        Serdes.String(),
                        JsonSerdes.ProcessedSpat()
                )
        );

        KStream<String, ProcessedSpat> inputStream = builder.stream(parameters.getSpatInputTopicName(), Consumed.with(Serdes.String(), JsonSerdes.ProcessedSpat()));

        inputStream
            .process(() -> new SpatMessageCountProgressionProcessor(parameters), processedSpatStateStore, latestSpatStateStore)
            .to(parameters.getSpatMessageCountProgressionEventOutputTopicName(), Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatMessageCountProgressionEvent()));

        return builder.build();
    }
}