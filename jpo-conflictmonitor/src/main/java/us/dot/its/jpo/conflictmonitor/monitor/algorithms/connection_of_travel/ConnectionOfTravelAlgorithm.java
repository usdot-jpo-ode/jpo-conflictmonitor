package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;


public interface ConnectionOfTravelAlgorithm extends Algorithm<ConnectionOfTravelParameters>{
    ConnectionOfTravelEvent getConnectionOfTravelEvent(VehiclePath path);
}

