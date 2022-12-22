package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment;

public interface LaneDirectionOfTravelAssessmentAlgorithmFactory {
    LaneDirectionOfTravelAssessmentAlgorithm getAlgorithm(String algorithmName);
}
