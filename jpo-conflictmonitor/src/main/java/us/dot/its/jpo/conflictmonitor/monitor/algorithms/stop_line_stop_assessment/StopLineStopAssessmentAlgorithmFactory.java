package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment;

public interface StopLineStopAssessmentAlgorithmFactory {
    StopLineStopAssessmentAlgorithm getAlgorithm(String algorithmName);
}
