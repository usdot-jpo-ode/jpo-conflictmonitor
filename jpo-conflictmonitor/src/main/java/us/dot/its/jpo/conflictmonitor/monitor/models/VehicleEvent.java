package us.dot.its.jpo.conflictmonitor.monitor.models;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Intersection;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;

/**
 * Represents a vehicle event containing BSMs, SPaTs, intersection information, and source.
 * Used to aggregate and process vehicle-related data within the Conflict Monitor system.
 */
@Getter
@Setter
@AllArgsConstructor
@Generated
public class VehicleEvent {

    /** Aggregator containing the Basic Safety Messages (BSMs) for the vehicle. */
    private BsmAggregator bsms;

    /** Aggregator containing the SPaT (Signal Phase and Timing) messages for the vehicle. */
    private SpatAggregator spats;

    /** The intersection associated with this vehicle event. */
    private Intersection intersection;

    /** The source of the vehicle event (e.g., device or system identifier). */
    private String source;
}
