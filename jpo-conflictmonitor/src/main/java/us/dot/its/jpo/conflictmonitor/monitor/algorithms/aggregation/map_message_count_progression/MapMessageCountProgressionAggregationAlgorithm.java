package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEventAggregation;

public interface MapMessageCountProgressionAggregationAlgorithm
    extends
        AggregationAlgorithmInterface<
            MapMessageCountProgressionEvent,
            MapMessageCountProgressionEventAggregation> {
}
