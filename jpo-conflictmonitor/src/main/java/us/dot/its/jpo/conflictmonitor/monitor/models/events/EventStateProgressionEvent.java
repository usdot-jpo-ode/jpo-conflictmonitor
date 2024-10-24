package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.SpatMovementStateTransition;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EventStateProgressionEvent extends Event {

    public EventStateProgressionEvent() {
        super("EventStateProgression");
    }

    String source;

    int signalGroupID;
    J2735MovementPhaseState eventStateA;
    long timestampA;
    J2735MovementPhaseState eventStateB;
    long timestampB;
}
