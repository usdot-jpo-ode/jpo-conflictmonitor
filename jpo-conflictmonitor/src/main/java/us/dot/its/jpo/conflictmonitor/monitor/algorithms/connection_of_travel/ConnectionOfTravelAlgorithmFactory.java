package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel;

public interface ConnectionOfTravelAlgorithmFactory {
    ConnectionOfTravelAlgorithm getAlgorithm(String algorithmName);
}
