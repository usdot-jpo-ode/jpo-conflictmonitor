package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

public interface StopLinePassageAlgorithm {
    StopLinePassageEvent getStopLinePassageEvent(StopLinePassageParameters parameters, VehiclePath path, SpatAggregator spats);
}






