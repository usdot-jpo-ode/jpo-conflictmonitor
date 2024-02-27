package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment;

public interface StopLinePassageAssessmentAlgorithmFactory {
    StopLinePassageAssessmentAlgorithm getAlgorithm(String algorithmName);
}
