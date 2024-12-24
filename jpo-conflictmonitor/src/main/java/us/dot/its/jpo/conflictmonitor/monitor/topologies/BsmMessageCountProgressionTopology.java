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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionStreamsAlgorithm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.processors.BsmMessageCountProgressionProcessor;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionConstants.DEFAULT_BSM_MESSAGE_COUNT_PROGRESSION_ALGORITHM;

// TODO Receive ProcessedBsm
@Component(DEFAULT_BSM_MESSAGE_COUNT_PROGRESSION_ALGORITHM)
@Slf4j
public class BsmMessageCountProgressionTopology
        extends BaseStreamsTopology<BsmMessageCountProgressionParameters>
        implements BsmMessageCountProgressionStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final String processedBsmStateStore = parameters.getProcessedBsmStateStoreName();
        final String latestBsmStateStore = parameters.getLatestBsmStateStoreName();
        final Duration retentionTime = Duration.ofMillis(parameters.getBufferTimeMs());

        builder.addStateStore(
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(processedBsmStateStore, retentionTime),
                        Serdes.String(),
                        JsonSerdes.ProcessedBsm()
                )
        );

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(latestBsmStateStore),
                        Serdes.String(),
                        JsonSerdes.ProcessedBsm()
                )
        );
        KStream<String, ProcessedBsm<Point>> inputStream = builder.stream(parameters.getBsmInputTopicName(), Consumed.with(Serdes.String(), JsonSerdes.ProcessedBsm()));

        inputStream
            .process(() -> new BsmMessageCountProgressionProcessor<>(parameters), processedBsmStateStore, latestBsmStateStore)
            .to(parameters.getBsmMessageCountProgressionEventOutputTopicName(), Produced.with(Serdes.String(), us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmMessageCountProgressionEvent()));

        return builder.build();
    }
}