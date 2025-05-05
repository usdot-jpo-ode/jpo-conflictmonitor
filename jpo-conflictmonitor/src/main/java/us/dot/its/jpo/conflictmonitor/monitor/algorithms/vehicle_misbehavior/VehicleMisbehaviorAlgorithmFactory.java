package us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior;

public interface VehicleMisbehaviorAlgorithmFactory {
    VehicleMisbehaviorAlgorithm getAlgorithm(String algorithmName);
}