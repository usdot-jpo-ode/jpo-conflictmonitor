package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementPhaseState;


/*
 * SignalStateConflictEvent - Signal state conflict events are generated when two crossing traffic movements produce conflicting signal states at the same time.
 * For example, if a 4 way intersection has all green lights, the east-west traffic, would cross the north-south traffic generating an event.
 * If there are multiple conflicting movements for a signal SPaT / MAP message pair, multiple SignalStateConflictEvents will be generated.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class SignalStateConflictEvent extends Event{

    // TODO rename properties, add ingress and egress lane IDs and type attributes per new specification
    /**
     * long representing the utc timestamp in milliseconds of when this event is generated
     */
    private long timestamp;

    /**
     * MovementPhaseState indicating what the signal state of the conflicting signal is
     */
    private MovementPhaseState conflictType;

    /**
     * int representing the signal group from the SPaT message of the first overlapping signal group
     */
    private int firstConflictingSignalGroup;

    /**
     * MovementPhaseState from the first signal group during the conflict
     */
    private MovementPhaseState firstConflictingSignalState;

    /**
     * int representing the signal group from the SPaT message of the second overlapping signal group
     */
    private int secondConflictingSignalGroup;

    /**
     * MovementPhaseState from the second signal group during the conflict
     */
    private MovementPhaseState secondConflictingSignalState;

    /**
     * String representing the source of the data for this event. Typically the intersection ID or RSU ip address.
     */
    private String source;

    public SignalStateConflictEvent(){
        super("SignalStateConflict");
    }
}
