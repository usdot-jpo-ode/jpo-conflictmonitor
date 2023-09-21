package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class SignalGroupAlignmentEvent extends Event{
    String sourceID;
    long timestamp;
    Set<Integer> spatSignalGroupIds;
    Set<Integer> mapSignalGroupIds;

    public SignalGroupAlignmentEvent(){
        super("SignalGroupAlignment");
    }

}
