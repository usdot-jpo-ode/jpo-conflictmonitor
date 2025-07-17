package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class BaseMinimumDataEventAggregation<TEvent extends BaseMinimumDataEvent>
        extends EventAggregation<TEvent> {

    public BaseMinimumDataEventAggregation(String eventType) {
        super(eventType);
    }

    // Linked hash set to remove duplicates, but maintain insertion order
    private final Set<String> missingDataElements = new LinkedHashSet<>();

    @Override
    public void update(TEvent event) {
        if (event.getMissingDataElements() != null) {
            missingDataElements.addAll(event.getMissingDataElements());
        }
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
