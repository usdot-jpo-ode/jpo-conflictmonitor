package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.SpatMinimumDataAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaAlgorithm;


public interface SpatValidationAlgorithm
    extends Algorithm<SpatValidationParameters> {

    SpatTimestampDeltaAlgorithm getTimestampDeltaAlgorithm();
    void setTimestampDeltaAlgorithm(SpatTimestampDeltaAlgorithm timestampDeltaAlgorithm);
    void setMinimumDataAggregationAlgorithm(SpatMinimumDataAggregationAlgorithm spatMinimumDataAggregationAlgorithm);
}
