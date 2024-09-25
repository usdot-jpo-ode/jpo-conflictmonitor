package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Contains two sequential {@link SpatMovementState}s from the same signal group.
 */
@Data
public class SpatMovementStateTransition {

    SpatMovementState firstEvent;
    SpatMovementState secondEvent;

    @JsonIgnore
    public int getSignalGroup() {
        if (firstEvent != null) {
            return firstEvent.getSignalGroup();
        } else if (secondEvent != null) {
            return secondEvent.getSignalGroup();
        } else {
            return -1;
        }
    }

    public SpatMovementStateTransition() {}

    public SpatMovementStateTransition(SpatMovementState first, SpatMovementState second) {
        verifySignalGroupsMatch(first, second);
        this.firstEvent = first;
        this.secondEvent = second;
    }

    public void setFirstEvent(SpatMovementState first) {
        verifySignalGroupsMatch(first, this.secondEvent);
        this.firstEvent = first;
    }

    public void setSecondEvent(SpatMovementState second) {
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

    private static void verifySignalGroupsMatch(SpatMovementState first, SpatMovementState second) {
        if (first == null | second == null) return;

        if (first.getSignalGroup() != second.getSignalGroup()) {
            throw new IllegalArgumentException(String.format("Signal groups don't match: %s != %s",
                    first.getSignalGroup(), second.getSignalGroup()));
        }
    }

}
