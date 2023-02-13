package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

public interface SignalStateVehicleCrossesAlgorithm {
    SignalStateEvent getSignalStateEvent(SignalStateVehicleCrossesParameters parameters, VehiclePath path, SpatAggregator spats);
}






