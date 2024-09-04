package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Contains two sequential {@link SpatMovementEvent}s from the same signal group.
 */
@Data
public class SpatMovementEventTransition {

    SpatMovementEvent firstEvent;
    SpatMovementEvent secondEvent;

    public SpatMovementEventTransition(SpatMovementEvent first, SpatMovementEvent second) {
        verifySignalGroupsMatch(first, second);
        this.firstEvent = first;
        this.secondEvent = second;
    }

    public void setFirstEvent(SpatMovementEvent first) {
        verifySignalGroupsMatch(first, this.secondEvent);
        this.firstEvent = first;
    }

    public void setSecondEvent(SpatMovementEvent second) {
        verifySignalGroupsMatch(this.firstEvent, second);
        this.secondEvent = second;
    }

    @JsonIgnore
    public PhaseStateTransition getStateTransition() {
         var pst = new PhaseStateTransition();
         if (firstEvent != null) pst.setFirstState(firstEvent.phaseState);
         if (secondEvent != null) pst.setSecondState(secondEvent.phaseState);
         return pst;
    }

    private static void verifySignalGroupsMatch(SpatMovementEvent first, SpatMovementEvent second) {
        if (first == null | second == null) return;

        if (first.getSignalGroup() != second.getSignalGroup()) {
            throw new IllegalArgumentException(String.format("Signal groups don't match: %s != %s",
                    first.getSignalGroup(), second.getSignalGroup()));
        }
    }
}
