package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression;

import lombok.*;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;

@Data
@Generated
public class BsmMessageCountProgressionAggregationKey implements RsuIdKey {
    private String rsuId;
    private String dataFrame;
    private String change;
}
