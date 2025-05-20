package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.RevocableEnabledLaneAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.RevocableEnabledLaneAlignmentEventAggregation;

public interface RevocableEnabledLaneAlignmentAggregationAlgolrithm
    extends AggregationAlgorithmInterface<
            RevocableEnabledLaneAlignmentEvent,
            RevocableEnabledLaneAlignmentEventAggregation> {
}
