package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatMovementPhaseState;

/**
 * StopLineStopEvent - StopLineStopEvents are generated each time a vehicle drives up to the stop line of an intersection. 
 * The generation of this event is not itself an indicator of problems with the intersection, but lots of invalid stop line stop events could indicate performance problems.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopLineStopEvent extends Event{

    /**
     * String representing the source of this event. Typically the Intersection ID or RSU IP address.
     */
    private String source;

    /**
     * int representing the lane ID from the MAP message of the ingress lane the vehicle is currently located in
     */
    private int ingressLane;

    /**
     * int representing the lane ID from the MAP message of the egress lane the vehicle is currently located in
     */
    private int egressLane;

    /**
     * int representing the connection number between the ingress and egress lanes. If no valid connection exists this will have a value of -1
     */
    private int connectionID;

    /**
     * MovementPhaseState describing the state of the light when the vehicle first stopped at the stop bar
     */
    private SpatMovementPhaseState initialEventState;

    /**
     * long representing the utc timestamp in milliseconds when the vehicle stopped.
     */
    private long initialTimestamp;

    /**
     * MovementPhaseState showing the final state of the light when the vehicle was no longer considered stopped
     */
    private SpatMovementPhaseState finalEventState;

    /**
     * long representing the utc timestamp in milliseconds when the vehicle was no longer considered stopped
     */
    private long finalTimestamp;

    /**
     * String representing the vehicle ID of the vehicle taken from the BSM message
     */
    private String vehicleID;

    /**
     * double representing the latitude of the vehicle when it stopped at the light
     */
    private double latitude;

    /**
     * double representing the longitude of the vehicle when it stopped at the light
     */
    private double longitude;

    /**
     * double representing the heading of the vehicle when it stopped at the light
     */
    private double heading;

    /**
     * int representing which signal group number the vehicle stopped at
     */
    private int signalGroup;

    /**
     * double representing the time in seconds that the vehicle was stopped while the light was red
     */
    private double timeStoppedDuringRed;
    
    /**
     * double representing the time in seconds that the vehicle was stopped while the light was yellow
     */
    private double timeStoppedDuringYellow;

    /**
     * double representing the time in seconds that the vehicle was stopped while the light was green
     */
    private double timeStoppedDuringGreen;

    /**
     * double representing the time in seconds that the vehicle was stopped while the light was dark
     */
    private double timeStoppedDuringDark;

    public StopLineStopEvent(){
        super("StopLineStop");
    }


    public String getKey(){
        return this.getRoadRegulatorID() + "_" + this.getIntersectionID() + "_" + this.getVehicleID();
    }




}
