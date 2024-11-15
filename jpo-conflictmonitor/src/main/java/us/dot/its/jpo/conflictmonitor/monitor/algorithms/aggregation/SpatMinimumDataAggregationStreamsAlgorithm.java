package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEventAggregation;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

public interface SpatMinimumDataAggregationStreamsAlgorithm
        extends
            SpatMinimumDataAggregationAlgorithm,
            AggregationStreamsAlgorithmInterface<
                    RsuIntersectionKey,
                    SpatMinimumDataEvent,
                    SpatMinimumDataEventAggregation> {
}
