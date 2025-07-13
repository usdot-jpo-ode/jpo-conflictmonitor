package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationAlgorithm;

public interface SpatTimeChangeDetailsAlgorithm
        extends Algorithm<SpatTimeChangeDetailsParameters> {

    void setAggregationAlgorithm(TimeChangeDetailsAggregationAlgorithm aggregationAlgorithm);

}
