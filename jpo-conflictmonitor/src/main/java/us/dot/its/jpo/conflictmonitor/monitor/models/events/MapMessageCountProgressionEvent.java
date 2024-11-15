package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class MapMessageCountProgressionEvent extends Event{

    private String messageType;
    private int messageCountA;
    private String timestampA;
    private int messageCountB;
    private String timestampB;

    public MapMessageCountProgressionEvent(){
        super("MapMessageCountProgression");
    }

}