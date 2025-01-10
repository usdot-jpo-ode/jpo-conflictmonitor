package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationStreamsAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEventAggregation;

public interface SignalStateConflictAggregationStreamsAlgorithm
    extends
        SignalStateConflictAggregationAlgorithm,
        AggregationStreamsAlgorithmInterface<
                SignalStateConflictAggregationKey,
                SignalStateConflictEvent,
                SignalStateConflictEventAggregation> {
}
