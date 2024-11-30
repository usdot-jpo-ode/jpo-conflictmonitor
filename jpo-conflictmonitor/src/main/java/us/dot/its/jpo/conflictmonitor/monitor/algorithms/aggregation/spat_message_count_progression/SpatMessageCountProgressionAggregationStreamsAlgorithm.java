package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEvent;

public interface SpatMessageCountProgressionAggregationStreamsAlgorithm
    extends
        AggregationAlgorithmInterface<
                SpatMessageCountProgressionEvent,
                SpatMessageCountProgressionEventAggregation> {
}
