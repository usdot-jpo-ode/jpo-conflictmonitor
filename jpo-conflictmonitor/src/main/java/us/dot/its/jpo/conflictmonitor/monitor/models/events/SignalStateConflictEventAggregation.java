package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

// TODO Update this and SignalStateConflictEvent to new definition supporting crosswalks/sidewalks
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public class SignalStateConflictEventAggregation
    extends EventAggregation<SignalStateConflictEvent>{

    public SignalStateConflictEventAggregation() {
        super("SignalStateConflictAggregation");
    }

    private J2735MovementPhaseState conflictType;
    private int firstConflictingSignalGroup;
    private J2735MovementPhaseState firstConflictingSignalState;
    private int secondConflictingSignalGroup;
    private J2735MovementPhaseState secondConflictingSignalState;

    @Override
    public void update(SignalStateConflictEvent event) {
        this.conflictType = event.getConflictType();
        this.firstConflictingSignalGroup = event.getFirstConflictingSignalGroup();
        this.firstConflictingSignalState = event.getFirstConflictingSignalState();
        this.secondConflictingSignalGroup = event.getSecondConflictingSignalGroup();
        this.secondConflictingSignalState = event.getSecondConflictingSignalState();
    }
}
