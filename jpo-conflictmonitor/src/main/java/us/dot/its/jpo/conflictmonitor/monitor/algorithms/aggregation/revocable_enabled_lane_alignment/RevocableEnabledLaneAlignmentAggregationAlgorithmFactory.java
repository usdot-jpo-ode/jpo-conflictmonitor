package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment;

public interface RevocableEnabledLaneAlignmentAggregationAlgorithmFactory {
    RevocableEnabledLaneAlignmentAggregationAlgorithm getAlgorithm(String algorithmName);
}
