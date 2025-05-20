package us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationAlgolrithm;

public interface RevocableEnabledLaneAlignmentAlgorithm
    extends ConfigurableAlgorithm<RevocableEnabledLaneAlignmentParameters> {

    void setAggregationAlgorithm(RevocableEnabledLaneAlignmentAggregationAlgolrithm aggregationAlgorithm);

}
