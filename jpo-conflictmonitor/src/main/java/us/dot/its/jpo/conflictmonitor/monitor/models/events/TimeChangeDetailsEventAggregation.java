package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
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

    private int signalGroupID;
    private String timeMarkTypeA;
    private String timeMarkTypeB;
    private J2735MovementPhaseState eventStateA;
    private J2735MovementPhaseState eventStateB;

    @Override
    public void update(TimeChangeDetailsEvent event) {
        this.signalGroupID = event.getSignalGroup();
        this.eventStateA = event.getFirstState();
        this.timeMarkTypeA = event.getFirstTimeMarkType();
        this.eventStateB = event.getSecondState();
        this.timeMarkTypeB = event.getSecondTimeMarkType();
    }
}
