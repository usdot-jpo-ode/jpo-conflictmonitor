package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEventAggregation;

public interface IntersectionReferenceAlignmentAggregationAlgorithm
    extends AggregationAlgorithmInterface<IntersectionReferenceAlignmentEvent, IntersectionReferenceAlignmentEventAggregation> {
}
