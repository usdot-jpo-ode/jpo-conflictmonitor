package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression;

import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;

@EqualsAndHashCode(callSuper = true)
@Data
@Generated
public class BsmMessageCountProgressionAggregationKey extends BsmRsuIdKey {
    private String dataFrame;
    private String change;
}
