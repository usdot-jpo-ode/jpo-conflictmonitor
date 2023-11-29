package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

public interface StopLineStopAlgorithm {
    StopLineStopEvent getStopLineStopEvent(StopLineStopParameters parameters, VehiclePath path, SpatAggregator spats);
}