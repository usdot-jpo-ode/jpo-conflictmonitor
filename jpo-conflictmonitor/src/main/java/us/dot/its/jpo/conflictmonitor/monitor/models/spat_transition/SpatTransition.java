package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

@Data
@Generated
public class SpatTransition {

    J2735MovementPhaseState firstState;
    J2735MovementPhaseState secondState;

}
