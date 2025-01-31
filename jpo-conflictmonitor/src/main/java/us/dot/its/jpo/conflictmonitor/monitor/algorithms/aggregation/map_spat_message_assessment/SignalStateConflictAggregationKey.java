package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

/**
 * Key to aggregate/deduplicate unique values for
 * {@link us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent}s
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SignalStateConflictAggregationKey
    extends RsuIntersectionKey {

    int conflictingSignalGroupA;
    J2735MovementPhaseState eventStateA;
    int conflictingSignalGroupB;
    J2735MovementPhaseState eventStateB;
    // TODO Add ingress and egress lane IDs and type attributes per new specification

}
