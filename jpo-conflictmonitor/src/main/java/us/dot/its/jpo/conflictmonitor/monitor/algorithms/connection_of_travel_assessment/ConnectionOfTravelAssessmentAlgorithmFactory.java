package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment;

public interface ConnectionOfTravelAssessmentAlgorithmFactory {
    ConnectionOfTravelAssessmentAlgorithm getAlgorithm(String algorithmName);
}
