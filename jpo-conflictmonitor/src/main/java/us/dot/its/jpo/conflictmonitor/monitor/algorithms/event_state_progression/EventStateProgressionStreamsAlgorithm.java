package us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

/**
 * Streams Algorithm, plugs into Message Ingest Topology
 */
public interface EventStateProgressionStreamsAlgorithm
    extends EventStateProgressionAlgorithm {

    // Consumes ProcessedSpats
    // Uses StreamsBuilder to add state store.
    // Timestamp extractor should be event time (as in the MessageIngestTopology), not odeReceivedAt.
    void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, ProcessedSpat> inputStream);
}
