package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

public interface SignalStateVehicleStopsAlgorithm {
    StopLineStopEvent getSignalStateStopEvent(SignalStateVehicleStopsParameters parameters, VehiclePath path, SpatAggregator spats);
}