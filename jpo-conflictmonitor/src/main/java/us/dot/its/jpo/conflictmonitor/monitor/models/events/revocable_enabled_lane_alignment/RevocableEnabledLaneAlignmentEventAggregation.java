package us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RevocableEnabledLaneAlignmentEventAggregation
    extends EventAggregation<RevocableEnabledLaneAlignmentEvent> {

    public RevocableEnabledLaneAlignmentEventAggregation() {
        super("RevocableEnabledLaneAlignmentAggregation");
    }

    private final Set<Integer> revocableLaneList = new TreeSet<Integer>();
    private final Set<Integer> enabledLaneList = new TreeSet<Integer>();

    @Override
    public void update(RevocableEnabledLaneAlignmentEvent event) {
        if (event.getRevocableLaneList() != null) {
            revocableLaneList.addAll(event.getRevocableLaneList());
        }
        if (event.getEnabledLaneList() != null) {
            enabledLaneList.addAll(event.getEnabledLaneList());
        }
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
