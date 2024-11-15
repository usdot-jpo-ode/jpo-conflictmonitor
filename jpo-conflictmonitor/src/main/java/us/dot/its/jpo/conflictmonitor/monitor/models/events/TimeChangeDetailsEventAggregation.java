package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TimeChangeDetailsEventAggregation
    extends EventAggregation<TimeChangeDetailsEvent> {

    public TimeChangeDetailsEventAggregation() {
        super("TimeChangeDetailsAggregation");
    }

    private int signalGroup;
    private String firstTimeMarkType;
    private String secondTimeMarkType;
    private J2735MovementPhaseState firstState;
    private J2735MovementPhaseState secondState;

    @Override
    public void update(TimeChangeDetailsEvent event) {
        this.signalGroup = event.getSignalGroup();
        this.firstState = event.getFirstState();
        this.firstTimeMarkType = event.getFirstTimeMarkType();
        this.secondState = event.getSecondState();
        this.secondTimeMarkType = event.getSecondTimeMarkType();
    }
}
