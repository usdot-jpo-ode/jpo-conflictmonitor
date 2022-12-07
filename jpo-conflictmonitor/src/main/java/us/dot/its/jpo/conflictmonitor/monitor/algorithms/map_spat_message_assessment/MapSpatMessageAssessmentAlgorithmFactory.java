package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment;

public interface MapSpatMessageAssessmentAlgorithmFactory {
    MapSpatMessageAssessmentAlgorithm getAlgorithm(String algorithmName);
}
