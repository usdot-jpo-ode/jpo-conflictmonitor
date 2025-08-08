package us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatMovementPhaseState;

@Data
@Generated
public class PhaseStateTransition {

    /**
     * The state before the transition
     */
    SpatMovementPhaseState stateA;

    /**
     * The state after the transition
     */
    SpatMovementPhaseState stateB;

    @Override
    public String toString() {
        return String.format("%s -> %s", stateA, stateB);
    }

}
