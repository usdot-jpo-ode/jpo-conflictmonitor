package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionStreamsAlgorithm;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionConstants.DEFAULT_SPAT_TRANSITION_ALGORITHM;

@Component(DEFAULT_SPAT_TRANSITION_ALGORITHM)
@Slf4j
public class SpatTransitionTopology
    extends BaseStreamsBuilder<SpatTransitionParameters>
    implements SpatTransitionStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, ProcessedSpat> inputStream) {

        inputStream
                // Ignore tombstones
                .filter(((rsuIntersectionKey, processedSpat) -> processedSpat != null))
                // Extract SpatMovementState from ProcessedSpat
                .mapValues(processedSpat -> {

                });
    }
}
