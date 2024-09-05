package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

@Data
@Generated
public class PhaseStateTransition {

    J2735MovementPhaseState firstState;
    J2735MovementPhaseState secondState;

    @Override
    public String toString() {
        return String.format("%s_%s", firstState, secondState);
    }

}
