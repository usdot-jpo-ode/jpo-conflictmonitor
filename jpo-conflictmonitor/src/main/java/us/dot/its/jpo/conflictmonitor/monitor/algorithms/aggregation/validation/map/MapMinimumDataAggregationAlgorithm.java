package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.map;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEventAggregation;

public interface MapMinimumDataAggregationAlgorithm
    extends AggregationAlgorithmInterface<MapMinimumDataEvent, MapMinimumDataEventAggregation> {
}
