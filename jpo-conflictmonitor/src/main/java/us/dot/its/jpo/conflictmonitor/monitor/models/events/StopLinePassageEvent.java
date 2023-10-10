package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Stop Line Passage Event
 * <p>Specified in Table 14, "CIMMS Software Design Document - FINAL", April 2023</p>
 */
@Document("CmStopLinePassageEvent")
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopLinePassageEvent extends Event {

    /**
     * Value used by the CIMMS to identify a unique intersection, such as an
     * index, numerical ID, IP address, intersection name, etc.
     */
    private String source;

    /**
     * Time from the vehicle BSM when the vehicle is considered to have
     * passed the stop line.  Epoch milliseconds.
     */
    private long timestamp;

    /**
     * Named "LaneID" in the design document.
     * <p>The lane ID of the lane that the BSM-producing vehicle is driving along.
     */
    private int ingressLane;

    // egressLane is not in the design document
    private int egressLane;

    // connectionID is not in the design document
    private int connectionID;

    /**
     * The event state of the signal group at the time the vehicle is considered
     * to have passed the stop line.
     */
    private J2735MovementPhaseState eventState;

    /**
     * The vehicle id of the vehicle from the BSM.
     */
    private String vehicleID;

    /**
     * The latitude (decimal degrees) of the vehicle from the BSM at the time
     * the vehicle is considered to have passed the stop line.
     */
    private double latitude;

    /**
     * The longitude (decimal degrees) of the vehicle from the BSM at the time
     * the vehicle is considered to have passed the stop line.
     */
    private double longitude;

    /**
     * The heading (decimal degrees, 0 is N increasing clockwise) of the
     * vehicle from the BSM at the time the vehicle is considered to have
     * passed the stop line.
     */
    private double heading;

    /**
     * The speed (mph) of the vehicle from the BSM at the time the vehicle is
     * considered to have passed the stop line.
     */
    private double speed;

    /**
     * The signal group associated with the movement the vehicle makes
     * through the intersection.
     */
    private int signalGroup;

    public StopLinePassageEvent(){
        super("StopLinePassage");
    }

    public String getKey(){
        return this.getRoadRegulatorID() + "_" + this.getIntersectionID() + "_" + this.getVehicleID();
    }



}
