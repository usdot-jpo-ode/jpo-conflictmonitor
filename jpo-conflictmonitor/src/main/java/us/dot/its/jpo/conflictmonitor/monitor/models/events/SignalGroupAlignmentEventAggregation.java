package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SignalGroupAlignmentEventAggregation
    extends EventAggregation<SignalGroupAlignmentEvent> {

    public SignalGroupAlignmentEventAggregation() {
        super("SignalGroupAlignmentAggregation");
    }

    private final Set<Integer> spatSignalGroupIds = new TreeSet<>();
    private final Set<Integer> mapSignalGroupIds = new TreeSet<>();

    @Override
    public void update(SignalGroupAlignmentEvent event) {
        if (event.getSpatSignalGroupIds() != null) {
            spatSignalGroupIds.addAll(event.getSpatSignalGroupIds());
        }
        if (event.getMapSignalGroupIds() != null) {
            mapSignalGroupIds.addAll(event.getMapSignalGroupIds());
        }
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
