package us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementPhaseState;

@Data
@Generated
public class PhaseStateTransition {

    /**
     * The state before the transition
     */
    MovementPhaseState stateA;

    /**
     * The state after the transition
     */
    MovementPhaseState stateB;

    @Override
    public String toString() {
        return String.format("%s -> %s", stateA, stateB);
    }

}
