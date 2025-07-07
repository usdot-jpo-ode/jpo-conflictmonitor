package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;

/**
 * Interface for Connection of Travel algorithms.
 * <p>
 * Extends the base {@link Algorithm} interface for Connection Of Travel Algorithms.
 */
public interface ConnectionOfTravelAlgorithm {

    /**
     * Gets the connection of travel algorithm used for validation.
     *
     * @return the connection of travel algorithm instance
     */
    ConnectionOfTravelEvent getConnectionOfTravelEvent(ConnectionOfTravelParameters parameters, VehiclePath path);
}

