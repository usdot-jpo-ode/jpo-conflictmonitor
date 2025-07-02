package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationStreamsAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEventAggregation;

public interface IntersectionReferenceAlignmentAggregationStreamsAlgorithm
    extends
        IntersectionReferenceAlignmentAggregationAlgorithm,
        AggregationStreamsAlgorithmInterface<
                String, // Key is the RSU ID string
                IntersectionReferenceAlignmentEvent,
                IntersectionReferenceAlignmentEventAggregation> {
}
