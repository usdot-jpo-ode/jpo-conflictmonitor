package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationStreamsAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEventAggregation;

public interface BsmMessageCountProgressionAggregationStreamsAlgorithm
    extends
        BsmMessageCountProgressionAggregationAlgorithm,
        AggregationStreamsAlgorithmInterface<
                        BsmMessageCountProgressionAggregationKey,
                        BsmMessageCountProgressionEvent,
                        BsmMessageCountProgressionEventAggregation> {
}
