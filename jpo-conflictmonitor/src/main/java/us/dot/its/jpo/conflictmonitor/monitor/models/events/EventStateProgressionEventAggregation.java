package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EventStateProgressionEventAggregation
    extends EventAggregation<EventStateProgressionEvent> {

    public EventStateProgressionEventAggregation() {
        super("EventStateProgressionAggregation");
    }

    int signalGroupID;
    J2735MovementPhaseState eventStateA;
    J2735MovementPhaseState eventStateB;

    @Override
    public void update(EventStateProgressionEvent event) {
        this.signalGroupID = event.getSignalGroupID();
        this.eventStateA = event.getEventStateA();
        this.eventStateB = event.getEventStateB();
        setNumberOfEvents(getNumberOfEvents() + 1);
     }
}
