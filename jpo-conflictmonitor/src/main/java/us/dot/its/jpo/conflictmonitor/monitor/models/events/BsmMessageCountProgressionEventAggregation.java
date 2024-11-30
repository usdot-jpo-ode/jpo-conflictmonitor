package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BsmMessageCountProgressionEventAggregation
    extends EventAggregation<BsmMessageCountProgressionEvent>{


    public BsmMessageCountProgressionEventAggregation() {
        super("BsmMessageCountProgressionAggregation");
    }

    private String messageType;
    private String dataFrame;
    private String change;

    @Override
    public void update(BsmMessageCountProgressionEvent event) {
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
