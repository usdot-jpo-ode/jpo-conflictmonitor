package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.Set;

/**
 * Key to aggregate/deduplicate unique values for
 * {@link us.dot.its.jpo.conflictmonitor.monitor.models.events.RevocableEnabledLaneAlignmentEvent}s
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RevocableEnabledLaneAlignmentAggregationKey
    extends RsuIntersectionKey {

    J2735MovementPhaseState eventState;
    Set<Integer> revocableLaneList;
    Set<Integer> enabledLaneList;

}
