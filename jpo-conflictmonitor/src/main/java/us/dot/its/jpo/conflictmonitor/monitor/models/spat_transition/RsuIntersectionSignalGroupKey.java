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

    public RsuIntersectionSignalGroupKey() {}

    public RsuIntersectionSignalGroupKey(RsuIntersectionKey intersectionKey) {
        this.setIntersectionId(intersectionKey.getIntersectionId());
        this.setRegion(intersectionKey.getRegion());
        this.setRsuId(intersectionKey.getRsuId());
    }

    public RsuIntersectionSignalGroupKey(RsuIntersectionKey intersectionKey, int signalGroup) {
        this(intersectionKey);
        this.signalGroup = signalGroup;
    }
}
