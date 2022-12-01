package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel;

import java.util.ArrayList;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;

public interface LaneDirectionOfTravelAlgorithm extends Algorithm<LaneDirectionOfTravelParameters>{
    ArrayList<LaneDirectionOfTravelEvent> getLaneDirectionOfTravelEvents(VehiclePath path);
}