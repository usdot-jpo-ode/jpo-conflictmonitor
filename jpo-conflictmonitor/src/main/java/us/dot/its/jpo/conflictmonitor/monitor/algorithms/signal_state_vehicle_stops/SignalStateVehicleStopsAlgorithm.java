package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

public interface SignalStateVehicleStopsAlgorithm extends Algorithm<SignalStateVehicleStopsParameters>{
    SignalStateStopEvent getSignalStateStopEvent(VehiclePath path, SpatAggregator spats);
}