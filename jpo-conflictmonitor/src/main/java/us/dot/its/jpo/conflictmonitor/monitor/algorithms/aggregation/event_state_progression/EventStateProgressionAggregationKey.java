package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression;

import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

/**
 * Key to aggregate/deduplicate unique values for {@link us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEvent}s
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EventStateProgressionAggregationKey
    extends RsuIntersectionSignalGroupKey {

    J2735MovementPhaseState eventStateA;
    J2735MovementPhaseState eventStateB;

}
