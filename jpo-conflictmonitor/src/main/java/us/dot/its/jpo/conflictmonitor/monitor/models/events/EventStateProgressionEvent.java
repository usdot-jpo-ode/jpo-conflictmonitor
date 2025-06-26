package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

/**
 * EventStateProgressionEvent - This event is generated when the light phase states in two sequential SPaT messages do not follow the allowable transition pattern (red -> green -> yellow -> red).
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EventStateProgressionEvent extends Event {

    public EventStateProgressionEvent() {
        super("EventStateProgression");
    }

    /**
    *  String representing the source of the message. Typically the intersection ID or RSU IP address of the message
    */
    String source;

    /**
    *  int representing the signal group ID of the violating signal head
    */
    int signalGroupID;

    /**
    *  J2735MovementPhaseState of the first signal state 
    */
    J2735MovementPhaseState eventStateA;

    /**
    *  long representing the utc timestamp in milliseconds when the first signal state took place
    */
    long timestampA;

    /**
    *  J2735MovementPhaseState of the second signal state 
    */
    J2735MovementPhaseState eventStateB;

    /**
    *  long representing the utc timestamp in milliseconds when the second signal state took place
    */
    long timestampB;
}
