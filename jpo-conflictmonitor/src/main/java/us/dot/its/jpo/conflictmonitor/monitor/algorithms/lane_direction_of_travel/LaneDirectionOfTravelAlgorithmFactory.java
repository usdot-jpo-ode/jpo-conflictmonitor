package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel;

public interface LaneDirectionOfTravelAlgorithmFactory {
    LaneDirectionOfTravelAlgorithm getAlgorithm(String algorithmName);
}
