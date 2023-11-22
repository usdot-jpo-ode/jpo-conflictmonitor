package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class SignalGroupAlignmentEvent extends Event{
    String source;
    long timestamp;
    Set<Integer> spatSignalGroupIds;
    Set<Integer> mapSignalGroupIds;

    public SignalGroupAlignmentEvent(){
        super("SignalGroupAlignment");
    }

}
