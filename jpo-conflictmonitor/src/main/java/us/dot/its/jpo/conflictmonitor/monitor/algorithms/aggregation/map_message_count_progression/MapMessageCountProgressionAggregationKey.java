package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

@Data
@EqualsAndHashCode(callSuper = true)
@Generated
public class MapMessageCountProgressionAggregationKey extends RsuIntersectionKey {
    private String dataFrame;
    private String change;
}
