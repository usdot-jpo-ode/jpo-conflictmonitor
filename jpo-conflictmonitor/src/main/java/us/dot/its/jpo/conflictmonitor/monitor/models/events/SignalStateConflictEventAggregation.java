package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

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
    private J2735MovementPhaseState eventStateA;
    private int conflictingSignalGroupB;
    private J2735MovementPhaseState eventStateB;
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
