package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel;

import java.util.ArrayList;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;

public interface LaneDirectionOfTravelAlgorithm {
    ArrayList<LaneDirectionOfTravelEvent> getLaneDirectionOfTravelEvents(LaneDirectionOfTravelParameters parameters, VehiclePath path);
}