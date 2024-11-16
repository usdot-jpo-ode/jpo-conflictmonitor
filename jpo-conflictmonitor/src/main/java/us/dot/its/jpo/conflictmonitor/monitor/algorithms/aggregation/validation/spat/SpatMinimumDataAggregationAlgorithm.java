package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEventAggregation;

public interface SpatMinimumDataAggregationAlgorithm
        extends AggregationAlgorithmInterface<SpatMinimumDataEvent, SpatMinimumDataEventAggregation> {
}
