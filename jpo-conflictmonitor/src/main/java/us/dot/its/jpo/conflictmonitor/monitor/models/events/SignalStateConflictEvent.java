package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;



@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class SignalStateConflictEvent extends Event{

    // TODO rename properties, add ingress and egress lane IDs and type attributes per new specification
    private long timestamp;
    private J2735MovementPhaseState conflictType;
    private int firstConflictingSignalGroup;
    private J2735MovementPhaseState firstConflictingSignalState;
    private int secondConflictingSignalGroup;
    private J2735MovementPhaseState secondConflictingSignalState;
    private String source;

    public SignalStateConflictEvent(){
        super("SignalStateConflict");
    }
}
