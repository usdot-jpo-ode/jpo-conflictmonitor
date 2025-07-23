package us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

/**
 * Mock of Revocable Lanes subtopology that does nothing for testing other parts of the Map/Spat Message Alignment
 * topology.
 */
public class MockRevocableEnabledLaneAlignmentStreamsAlgorithm implements RevocableEnabledLaneAlignmentStreamsAlgorithm {

    @Override
    public void setAggregationAlgorithm(RevocableEnabledLaneAlignmentAggregationAlgorithm aggregationAlgorithm) {
        // Do nothing
    }

    @Override
    public void setParameters(RevocableEnabledLaneAlignmentParameters revocableEnabledLaneAlignmentParameters) {
        // Do nothing
    }

    @Override
    public RevocableEnabledLaneAlignmentParameters getParameters() {
        return new RevocableEnabledLaneAlignmentParameters();
    }

    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, SpatMap> spatMapStream) {
        // Do nothing
    }

}
