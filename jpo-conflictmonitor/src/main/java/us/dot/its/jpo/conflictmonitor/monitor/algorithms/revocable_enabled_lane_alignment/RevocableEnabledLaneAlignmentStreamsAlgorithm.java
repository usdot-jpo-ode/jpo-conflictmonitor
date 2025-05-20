package us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

public interface RevocableEnabledLaneAlignmentStreamsAlgorithm extends RevocableEnabledLaneAlignmentAlgorithm {

    void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, SpatMap> spatMapStream);

}
