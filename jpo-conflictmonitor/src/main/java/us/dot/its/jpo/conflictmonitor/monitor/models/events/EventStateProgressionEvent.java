package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.SpatMovementStateTransition;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EventStateProgressionEvent extends Event {

    public EventStateProgressionEvent() {
        super("EventStateProgression");
    }

    String source;

    SpatMovementStateTransition transition;

    @JsonIgnore
    public int getSignalGroup() {
        if (transition == null) return -1;
        return transition.getSignalGroup();
    }
}
