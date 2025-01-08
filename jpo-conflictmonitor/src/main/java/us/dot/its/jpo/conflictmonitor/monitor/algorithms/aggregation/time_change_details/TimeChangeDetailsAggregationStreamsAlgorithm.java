package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationStreamsAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEventAggregation;

public interface TimeChangeDetailsAggregationStreamsAlgorithm
    extends
        TimeChangeDetailsAggregationAlgorithm,
        AggregationStreamsAlgorithmInterface<
                TimeChangeDetailsAggregationKey,
                TimeChangeDetailsEvent,
                TimeChangeDetailsEventAggregation> {
}
