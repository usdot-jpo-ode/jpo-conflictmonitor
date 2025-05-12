package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
 * Vehicle Misbehavior Events are generated when vehicles make maneuvers which 
 */

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class VehicleMisbehaviorEvent extends Event{

    public VehicleMisbehaviorEvent() {
        super("VehicleMisbehavior");
    }

    String source;

    /**
     * Long representing the utc timestamp of the BSM message
     */
    long timeStamp;

    /*
     * J2735 Vehicle ID of the BSM
     */
    String vehicleID;

    /**
     * The vehicle speed expressed in MPH.
     */
    double reportedSpeed;

    /**
     * User configurable range for generating a misbehavior event due to unrealistic speed. Measured in MPH.
     */
    double speedRange;
    
    /**
     * The yaw rate of the vehicle, representing the heading change rate, expressed in degrees / second.
     */
    double reportedYawRate;

    /**
     * The acceleration of the vehicle in the lateral direction. Reported in Ft / second / second.
     */
    double reportedAccelerationLat;

    /**
     * The acceleration of the vehicle in the longitudinal direction. Reported in Ft / second / second
     */
    double reportedAccelerationLon;

    /**
     * The acceleration of the vehicle in the vertical direction. Reported in Ft / second / second.
     */
    double reportedAccelerationVert;

    /**
     * Configurable range for generating a misbehavior event due to unrealistic lateral accelerations. Reported in Ft / second / second.
     */
    double accelerationRangeLat;

    /**
     * Configurable range for generating a misbehavior event due to unrealistic longitudinal accelerations. Reported in Ft / second / second.
     */
    double accelerationRangeLon;

    /**
     * Configurable range for generating a misbehavior event due to unrealistic vertical accelerations. Reported in Ft / second / second.
     */
    double accelerationRangeVert;

    /**
     * The current heading of the vehicle, expressed in degrees from north.
     */
    double reportedHeading;

    /**
     * Calculated Speed - Speed calculated by obtaining the distance traveled between two consecutive BSM's and dividing by the elapsed time. Reported in MPH.
     */
    double calculatedSpeed;

    /**
     *  Heading calculated by changes in the vehicles position from two consecutive BSM's reported in degrees
     */
    double calculatedYawRate;

    
}
