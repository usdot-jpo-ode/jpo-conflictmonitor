package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

@Data
@Generated
public class PhaseStateTransition {

    /**
     * The state before the transition
     */
    J2735MovementPhaseState stateA;

    /**
     * The state after the transition
     */
    J2735MovementPhaseState stateB;

    @Override
    public String toString() {
        return String.format("%s -> %s", stateA, stateB);
    }

}
