package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;

import java.util.LinkedHashSet;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public abstract class BaseMinimumDataEventAggregation<TEvent extends BaseMinimumDataEvent>
        extends EventAggregation<TEvent> {

    public BaseMinimumDataEventAggregation(String eventType) {
        super(eventType);
    }

    // Linked hash set to remove duplicates, but maintain insertion order
    private final LinkedHashSet<String> missingDataElements = new LinkedHashSet<>();

    @Override
    public void update(TEvent event) {
        if (event.getMissingDataElements() != null) {
            getMissingDataElements().addAll(event.getMissingDataElements());
        }
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
