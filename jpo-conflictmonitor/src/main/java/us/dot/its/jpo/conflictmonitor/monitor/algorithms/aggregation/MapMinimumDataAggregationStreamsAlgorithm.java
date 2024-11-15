package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEventAggregation;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

public interface MapMinimumDataAggregationStreamsAlgorithm
    extends
        MapMinimumDataAggregationAlgorithm,
        AggregationStreamsAlgorithmInterface<
                RsuIntersectionKey,
                MapMinimumDataEvent,
                MapMinimumDataEventAggregation> {
}
