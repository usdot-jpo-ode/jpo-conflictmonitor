package us.dot.its.jpo.conflictmonitor.monitor.algorithms.assessment;

public interface AssessmentAlgorithmFactory {
    AssessmentAlgorithm getAlgorithm(String algorithmName);
}
