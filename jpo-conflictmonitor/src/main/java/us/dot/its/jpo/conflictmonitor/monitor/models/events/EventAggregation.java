package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
 * @param <TEvent> Type of the event to be aggregated
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public abstract class EventAggregation<TEvent extends Event> extends Event {

    public EventAggregation(String eventType) {
        super(eventType);
    }

    String source;
    ProcessingTimePeriod timePeriod;
    int numberOfEvents;

    /**
     * Update the event aggregation
     * @param event The event
     */
    public abstract void update(TEvent event);

}
