package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEventAggregation;

public interface SignalGroupAlignmentAggregationAlgorithm
    extends AggregationAlgorithmInterface<SignalGroupAlignmentEvent, SignalGroupAlignmentEventAggregation> {
}
