package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RtcmMessageCountProgressionEventAggregation
    extends EventAggregation<RtcmMessageCountProgressionEvent> {

    public RtcmMessageCountProgressionEventAggregation() {
        super("RtcmMessageCountProgressionAggregation");
    }

    private String messageType;
    private String dataFrame;
    private String change;

    @Override
    public void update(RtcmMessageCountProgressionEvent event) {
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
