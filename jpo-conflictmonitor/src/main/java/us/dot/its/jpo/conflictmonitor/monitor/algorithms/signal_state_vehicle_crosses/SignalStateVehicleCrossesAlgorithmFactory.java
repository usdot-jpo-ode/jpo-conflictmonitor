package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses;

public interface SignalStateVehicleCrossesAlgorithmFactory {
    StopLinePassageAlgorithm getAlgorithm(String algorithmName);
}
