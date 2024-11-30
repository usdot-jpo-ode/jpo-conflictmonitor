package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationStreamsAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEventAggregation;

public interface MapMessageCountProgressionAggregationStreamsAlgorithm
    extends
        MapMessageCountProgressionAggregationAlgorithm,
        AggregationStreamsAlgorithmInterface<
                MapMessageCountProgressionAggregationKey,
                MapMessageCountProgressionEvent,
                MapMessageCountProgressionEventAggregation> {
}
