package us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition;


import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

/**
 * Key that includes signal group
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RsuIntersectionSignalGroupKey extends RsuIntersectionKey {
    int signalGroup;
}
