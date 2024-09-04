package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;

import lombok.Data;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.time.ZonedDateTime;

/**
 * Key information from one movement state/event from a SPaT in a flat format
 */
@Data
public class SpatMovementState {

    // Fields from ProcessedSpat
    long odeReceivedAt;
    long utcTimeStamp;
    int revision;

    // Fields from MovementState
    int signalGroup;

    // Fields from the first MovementEvent of the MovementState (additional MovementEvents are ignored)
    J2735MovementPhaseState phaseState;

    // Fields from TimeChangeDetails
    long startTime;
    long minEndTime;
    long maxEndTime;
}
