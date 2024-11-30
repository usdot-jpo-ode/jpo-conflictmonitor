package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEventAggregation;

public interface BsmMessageCountProgressionAggregationAlgorithm
    extends
        AggregationAlgorithmInterface<
            BsmMessageCountProgressionEvent,
            BsmMessageCountProgressionEventAggregation> {
}
