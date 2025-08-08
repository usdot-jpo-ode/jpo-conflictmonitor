package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression;

import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatMovementPhaseState;

/**
 * Key to aggregate/deduplicate unique values for {@link us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEvent}s
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EventStateProgressionAggregationKey
    extends RsuIntersectionSignalGroupKey {

    SpatMovementPhaseState eventStateA;
    SpatMovementPhaseState eventStateB;

}
