package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class SpatMessageCountProgressionEventAggregation
    extends EventAggregation<SpatMessageCountProgressionEvent> {

    public SpatMessageCountProgressionEventAggregation() {
        super("SpatMessageCountProgressionAggregation");
    }

    private String messageType;
    private String dataFrame;
    private String change;

    @Override
    public void update(SpatMessageCountProgressionEvent event) {
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
