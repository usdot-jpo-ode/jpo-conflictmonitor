package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;

import lombok.Data;
1
import us.dot.its.jpo.conflictmonitor.monitor.utils.DateTimeUtils;
import us.dot.its.jpo.conflictmonitor.monitor.utils.SpatUtils;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.pojos.spat.TimingChangeDetails;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.ArrayList;
import java.util.List;

import static us.dot.its.jpo.conflictmonitor.monitor.utils.DateTimeUtils.toMillis;

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

    // Fields from the first MovementEvent of the MovementState (additional MovementEvents representing the future are
    // ignored)
    J2735MovementPhaseState phaseState;

    // Fields from TimeChangeDetails
    long startTime;
    long minEndTime;
    long maxEndTime;

    public static List<SpatMovementState> fromProcessedSpat(ProcessedSpat spat) {
        List<SpatMovementState> stateList = new ArrayList<>();
        if (spat.getStates() == null) return stateList;
        final long ingestTime = SpatUtils.getOdeReceivedAt(spat);
        final long eventTime = SpatUtils.getTimestamp(spat);
        for (MovementState state : spat.getStates()) {
            var sms = new SpatMovementState();
            sms.setOdeReceivedAt(ingestTime);
            sms.setUtcTimeStamp(eventTime);
            int revision = spat.getRevision() != null ? spat.getRevision() : -1;
            sms.setRevision(revision);
            int signalGroup = state.getSignalGroup() != null ? state.getSignalGroup() : -1;
            sms.setSignalGroup(signalGroup);
            List<MovementEvent> movementEventList = state.getStateTimeSpeed();
            MovementEvent firstMovementEvent =
                    movementEventList != null && !movementEventList.isEmpty() ? movementEventList.getFirst() : null;
            sms.setPhaseState(firstMovementEvent.getEventState());
            TimingChangeDetails timing = firstMovementEvent.getTiming();
            sms.setStartTime(toMillis(timing.getStartTime()));
            sms.setMaxEndTime(toMillis(timing.getMaxEndTime()));
            sms.setMinEndTime(toMillis(timing.getMinEndTime()));
            stateList.add(sms);
        }
        return stateList;
    }
}
