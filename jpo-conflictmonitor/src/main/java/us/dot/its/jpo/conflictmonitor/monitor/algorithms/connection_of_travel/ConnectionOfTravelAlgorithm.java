package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;


public interface ConnectionOfTravelAlgorithm {
    ConnectionOfTravelEvent getConnectionOfTravelEvent(ConnectionOfTravelParameters parameters, VehiclePath path);
}

