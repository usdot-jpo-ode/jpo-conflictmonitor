package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * Streams Algorithm, plugs into Message Ingest Topology
 */
public interface SpatTransitionStreamsAlgorithm {

    // Consumes ProcessedSpats
    // Uses StreamsBuilder to add state store.
    // Timestamp extractor should be event time (as in the MessageIngestTopology), not odeReceivedAt.
    void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, ProcessedSpat> inputStream);
}
