package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

/**
* SignalGroupAlignmentEvent - Signal Group Alignment Events are generated whenever a matching pair of MAP and SPaT messages do not have the same signal groups defined.
* The event contains a set of signal group ID's for both the MAP and SPaT event for comparison
*/
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class SignalGroupAlignmentEvent extends Event{

    /**
     * String representing the source of this event. Typically the Intersection ID or the IP address of the RSU unit
     */
    String source;

    /**
     * long representing the UTC timestamp in milliseconds of when the SPaT and MAP message alignment error occurred.
     */
    long timestamp;

    /**
     * Set of integer signal group IDs from the SPaT message
     */
    Set<Integer> spatSignalGroupIds;

    /**
     * Set of integer signal group IDs from the MAP message
     */
    Set<Integer> mapSignalGroupIds;

    public SignalGroupAlignmentEvent(){
        super("SignalGroupAlignment");
    }

}
