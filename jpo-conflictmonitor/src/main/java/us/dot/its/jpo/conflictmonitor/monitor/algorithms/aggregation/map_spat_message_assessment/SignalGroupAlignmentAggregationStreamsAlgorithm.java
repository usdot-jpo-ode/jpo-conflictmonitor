package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationStreamsAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEventAggregation;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

public interface SignalGroupAlignmentAggregationStreamsAlgorithm
    extends
        SignalGroupAlignmentAggregationAlgorithm,
        AggregationStreamsAlgorithmInterface<
                RsuIntersectionKey,
                SignalGroupAlignmentEvent,
                SignalGroupAlignmentEventAggregation> {
}
