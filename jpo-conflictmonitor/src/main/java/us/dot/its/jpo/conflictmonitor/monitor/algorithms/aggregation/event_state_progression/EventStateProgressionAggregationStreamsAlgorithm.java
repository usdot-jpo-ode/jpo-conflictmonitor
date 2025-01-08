package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationStreamsAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEventAggregation;

public interface EventStateProgressionAggregationStreamsAlgorithm
    extends
        EventStateProgressionAggregationAlgorithm,
        AggregationStreamsAlgorithmInterface<
                EventStateProgressionAggregationKey,
                EventStateProgressionEvent,
                EventStateProgressionEventAggregation> {
}
