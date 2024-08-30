package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * Streams Algirithm, plugs into Spat Validation Topology
 */
public interface SpatTransitionStreamsAlgorithm {

    // Consumes ProcessedSpats
    void buildTopology(KStream<RsuIntersectionKey, ProcessedSpat> inputStream);
}
