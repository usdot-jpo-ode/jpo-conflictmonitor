package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MapMessageCountProgressionEventAggregation
    extends EventAggregation<MapMessageCountProgressionEvent> {

    public MapMessageCountProgressionEventAggregation() {
        super("MapMessageCountProgressionAggregation");
    }

    private String messageType;
    private String dataFrame;
    private String change;

    @Override
    public void update(MapMessageCountProgressionEvent event) {
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
