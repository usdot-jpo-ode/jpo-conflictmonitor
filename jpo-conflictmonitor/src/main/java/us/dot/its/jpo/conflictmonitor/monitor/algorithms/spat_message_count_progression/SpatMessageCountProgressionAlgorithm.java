package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationAlgorithm;

public interface SpatMessageCountProgressionAlgorithm extends Algorithm<SpatMessageCountProgressionParameters>{

    void setAggregationAlgorithm(SpatMessageCountProgressionAggregationAlgorithm aggregationAlgorithm);

}






