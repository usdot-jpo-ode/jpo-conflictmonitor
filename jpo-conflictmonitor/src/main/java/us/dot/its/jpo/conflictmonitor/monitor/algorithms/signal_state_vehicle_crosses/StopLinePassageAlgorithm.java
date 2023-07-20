package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

public interface StopLinePassageAlgorithm {
    StopLinePassageEvent getStopLinePassageEvent(StopLinePassageParameters parameters, VehiclePath path, SpatAggregator spats);
}






