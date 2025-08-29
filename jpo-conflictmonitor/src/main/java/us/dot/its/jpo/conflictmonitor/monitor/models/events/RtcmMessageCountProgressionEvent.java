package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class RtcmMessageCountProgressionEvent extends Event {
    public RtcmMessageCountProgressionEvent() {
        super("RtcmMessageCountProgression");
    }
}
