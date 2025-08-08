package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatMovementPhaseState;

/**
 * Key to aggregate/deduplicate unique values for
 * {@link us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent}s
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TimeChangeDetailsAggregationKey
    extends RsuIntersectionSignalGroupKey {

    String timeMarkTypeA;
    String timeMarkTypeB;
    SpatMovementPhaseState eventStateA;
    SpatMovementPhaseState eventStateB;

}
