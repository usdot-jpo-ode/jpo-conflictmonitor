package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops;

public interface SignalStateVehicleStopsAlgorithmFactory {
    SignalStateVehicleStopsAlgorithm getAlgorithm(String algorithmName);
}