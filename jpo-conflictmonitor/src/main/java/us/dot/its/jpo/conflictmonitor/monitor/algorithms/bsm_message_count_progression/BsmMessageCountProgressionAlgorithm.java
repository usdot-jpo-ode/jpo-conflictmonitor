package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationAlgorithm;

public interface BsmMessageCountProgressionAlgorithm
        extends Algorithm<BsmMessageCountProgressionParameters>{

    void setAggregationAlgorithm(BsmMessageCountProgressionAggregationAlgorithm aggregationAlgorithm);

}






