package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEventAggregation;

public interface SpatMessageCountProgressionAggregationAlgorithm
    extends
        AggregationAlgorithmInterface<
                SpatMessageCountProgressionEvent,
                SpatMessageCountProgressionEventAggregation> {
}
