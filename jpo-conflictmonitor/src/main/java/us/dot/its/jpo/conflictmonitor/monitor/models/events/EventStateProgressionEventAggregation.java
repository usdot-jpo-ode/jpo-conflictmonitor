package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementPhaseState;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EventStateProgressionEventAggregation
    extends EventAggregation<EventStateProgressionEvent> {

    public EventStateProgressionEventAggregation() {
        super("EventStateProgressionAggregation");
    }

    int signalGroupID;
    ProcessedMovementPhaseState eventStateA;
    ProcessedMovementPhaseState eventStateB;

    @Override
    public void update(EventStateProgressionEvent event) {
        this.signalGroupID = event.getSignalGroupID();
        this.eventStateA = event.getEventStateA();
        this.eventStateB = event.getEventStateB();
        setNumberOfEvents(getNumberOfEvents() + 1);
     }
}
