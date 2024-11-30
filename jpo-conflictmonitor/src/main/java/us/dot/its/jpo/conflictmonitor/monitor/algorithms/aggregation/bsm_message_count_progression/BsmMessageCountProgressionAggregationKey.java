package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;

@Getter
@Setter
@EqualsAndHashCode
public class BsmMessageCountProgressionAggregationKey implements RsuIdKey {
    private String rsuId;


    private String dataFrame;
    private String change;
}
