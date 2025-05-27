package us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment;

public interface RevocableEnabledLaneAlignmentAlgorithmFactory {
    RevocableEnabledLaneAlignmentAlgorithm getAlgorithm(String algorithmName);
}
