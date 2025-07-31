package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.MovementPhaseState;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.RsuIntersectionSignalGroupKey;

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
    MovementPhaseState eventStateA;
    MovementPhaseState eventStateB;

}
