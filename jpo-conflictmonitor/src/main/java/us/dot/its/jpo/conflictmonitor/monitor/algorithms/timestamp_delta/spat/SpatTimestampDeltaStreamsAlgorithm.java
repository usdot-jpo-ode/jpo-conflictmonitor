package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat;

import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

public interface SpatTimestampDeltaStreamsAlgorithm
        extends SpatTimestampDeltaAlgorithm {
    KStream<RsuIntersectionKey, ProcessedSpat> buildTopology(KStream<RsuIntersectionKey, ProcessedSpat> inputStream);
}
