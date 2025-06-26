package us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression;

import lombok.Data;

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

    /**
     * long representing the UTC time in milliseconds when the ODE received the SPaT message.
     */
    long odeReceivedAt;

    /**
     * long representing the UTC time in milliseconds when the SPaT message was broadcast. This field is generated based upon the time fields of the SPaT message in conjunction with the ODE received at time. 
     */
    long utcTimeStamp;

    /**
     * int representing the revision number of the SPaT message. Breaking changes to the SPaT message data structure will cause an increment to this value.
     */
    int revision;

    // Fields from MovementState
    /**
     * int representing the signal group associated with the SPaT message movement state
     */
    int signalGroup;

    /**
     * A J2735MovementPhaseState representing the first MovementEvent of the MovementState. Additional Movement events representing the future are ignored.
     */
    J2735MovementPhaseState phaseState;

    // Fields from TimeChangeDetails
    /**
     * long representing the UTC time in milliseconds when this phaseState will begin
     */
    long startTime;

    /**
     * long representing the UTC time in milliseconds representing the earliest time when the phaseState will end
     */
    long minEndTime;

    /**
     * long representing the UTC time in milliseconds representing the latest time when the phaseState will end
     */
    long maxEndTime;


    /**
     * Helper function to create SpatMovementState objects from the ProcessedSpat message. 
     */
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
