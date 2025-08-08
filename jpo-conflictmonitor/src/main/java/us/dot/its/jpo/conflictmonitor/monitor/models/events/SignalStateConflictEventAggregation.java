package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatMovementPhaseState;

// TODO Update this and SignalStateConflictEvent to new definition supporting crosswalks/sidewalks
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SignalStateConflictEventAggregation
    extends EventAggregation<SignalStateConflictEvent>{

    public SignalStateConflictEventAggregation() {
        super("SignalStateConflictAggregation");
    }

    private int conflictingSignalGroupA;
    private SpatMovementPhaseState eventStateA;
    private int conflictingSignalGroupB;
    private SpatMovementPhaseState eventStateB;
    // TODO Add ingress and egress lane IDs and type attributes per new specification

    @Override
    public void update(SignalStateConflictEvent event) {
        this.conflictingSignalGroupA = event.getFirstConflictingSignalGroup();
        this.eventStateA = event.getFirstConflictingSignalState();
        this.conflictingSignalGroupB = event.getSecondConflictingSignalGroup();
        this.eventStateB = event.getSecondConflictingSignalState();
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
