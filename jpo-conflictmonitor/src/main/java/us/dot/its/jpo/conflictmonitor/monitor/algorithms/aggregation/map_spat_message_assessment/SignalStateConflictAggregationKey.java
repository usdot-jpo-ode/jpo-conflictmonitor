package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementPhaseState;

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
    ProcessedMovementPhaseState eventStateA;
    int conflictingSignalGroupB;
    ProcessedMovementPhaseState eventStateB;
    // TODO Add ingress and egress lane IDs and type attributes per new specification

}
