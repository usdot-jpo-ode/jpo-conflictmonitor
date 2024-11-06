package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public class SpatMinimumDataEventAggregation extends EventAggregation<SpatMinimumDataEvent> {

    public SpatMinimumDataEventAggregation() {
        super("SpatMinimumDataAggregation");
    }

    // Linked hash set to remove duplicates, but maintain insertion order
    final LinkedHashSet<String> missingDataElements = new LinkedHashSet<>();

    @Override
    public void update(SpatMinimumDataEvent event) {
        if (event.getMissingDataElements() != null) {
            getMissingDataElements().addAll(event.getMissingDataElements());
        }
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
