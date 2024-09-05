package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatMovementStateTransition;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class IllegalSpatTransitionEvent extends Event {

    public IllegalSpatTransitionEvent() {
        super("IllegalSpatTransition");
    }

    String source;

    SpatMovementStateTransition transition;

    @JsonIgnore
    public int getSignalGroup() {
        if (transition == null) return -1;
        return transition.getSignalGroup();
    }
}
