package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.FixedKeyProcessor;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorSupplier;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.bsm.BsmTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.bsm.BsmTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.ode.model.OdeBsmData;

@Slf4j
public class BsmTimestampDeltaTopology
    extends BaseStreamsBuilder<BsmTimestampDeltaParameters>
    implements BsmTimestampDeltaStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public StreamsBuilder buildTopology(StreamsBuilder builder) {

        var bsmStream =
        builder.stream(
                parameters.getInputTopicName(),
                Consumed.with(
                        JsonSerdes.BsmRsuIdKey(),
                        JsonSerdes.OdeBsm(),
                        new BsmTimestampExtractor(),
                        Topology.AutoOffsetReset.LATEST)
        ).processValues(BsmTimestampDeltaProcessor::new);



        return builder;
    }
}
