package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.IntersectionRegion;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BsmIntersectionIdKey extends RsuIntersectionKey {

    String bsmId;

    public BsmIntersectionIdKey() {}

    public BsmIntersectionIdKey(String bsmId, String rsuId, int intersectionId) {
        super(rsuId, intersectionId);
        this.bsmId = bsmId;
    }

    public BsmIntersectionIdKey(String bsmId, String rsuId, int intersectionId, int region) {
        super(rsuId, intersectionId, region);
        this.bsmId = bsmId;
    }

    @JsonIgnore
    public IntersectionRegion getIntersectionRegion() {
        return new IntersectionRegion(getIntersectionId(), getRegion());
    }

}
