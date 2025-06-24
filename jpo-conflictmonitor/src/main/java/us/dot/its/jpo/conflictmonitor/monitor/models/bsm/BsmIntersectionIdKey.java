package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Generated
public class BsmIntersectionIdKey extends RsuIntersectionKey {

    String bsmId;
    String logId;

    public BsmIntersectionIdKey() {}

    public BsmIntersectionIdKey(String bsmId, String rsuId, int intersectionId, String logId) {
        super(rsuId, intersectionId);
        this.bsmId = bsmId;
        this.logId = logId;
    }

    public BsmIntersectionIdKey(String bsmId, String rsuId, int intersectionId, int region, String logId) {
        super(rsuId, intersectionId, region);
        this.bsmId = bsmId;
        this.logId = logId;
    }

    @JsonIgnore
    public IntersectionRegion getIntersectionRegion() {
        return new IntersectionRegion(getIntersectionId(), getRegion());
    }

}
