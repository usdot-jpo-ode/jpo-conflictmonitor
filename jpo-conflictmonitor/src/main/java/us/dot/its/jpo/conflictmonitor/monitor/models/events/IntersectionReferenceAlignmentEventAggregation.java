package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.RegulatorIntersectionId;

import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class IntersectionReferenceAlignmentEventAggregation
    extends EventAggregation<IntersectionReferenceAlignmentEvent> {

    public IntersectionReferenceAlignmentEventAggregation() {
        super("IntersectionReferenceAlignmentAggregation");
    }

    // Sorted sets
    private final Set<RegulatorIntersectionId> spatRegulatorIntersectionIds = new TreeSet<>();
    private final Set<RegulatorIntersectionId> mapRegulatorIntersectionIds = new TreeSet<>();

    @Override
    public void update(IntersectionReferenceAlignmentEvent event) {
        if (event.getSpatRegulatorIntersectionIds() != null) {
            spatRegulatorIntersectionIds.addAll(event.getSpatRegulatorIntersectionIds());
        }
        if (event.getMapRegulatorIntersectionIds() != null) {
            mapRegulatorIntersectionIds.addAll(event.getMapRegulatorIntersectionIds());
        }
        setNumberOfEvents(getNumberOfEvents() + 1);
    }
}
