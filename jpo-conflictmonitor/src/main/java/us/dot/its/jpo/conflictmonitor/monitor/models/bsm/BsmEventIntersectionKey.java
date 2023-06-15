package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.IntersectionRegion;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public class BsmEventIntersectionKey extends BsmIntersectionKey {

    public BsmEventIntersectionKey() {}

    public BsmEventIntersectionKey(BsmIntersectionKey superKey) {
        super(superKey.getRsuId(), superKey.getBsmId());
    }

    public BsmEventIntersectionKey(BsmIntersectionKey superKey, IntersectionRegion intersectionRegion) {
        super(superKey.getRsuId(), superKey.getBsmId());
        setIntersectionRegion(intersectionRegion);
    }

    private Integer intersectionId;
    private Integer region;

    @JsonIgnore
    public BsmIntersectionKey getBsmIntersectionKey() {
        return new BsmIntersectionKey(getRsuId(), getBsmId());
    }

    @JsonIgnore
    public IntersectionRegion getIntersectionRegion() {
        return new IntersectionRegion(intersectionId, region);
    }

    @JsonIgnore
    public void setIntersectionRegion(IntersectionRegion intersectionRegion) {
        this.intersectionId = intersectionRegion.getIntersectionId();
        this.region = intersectionRegion.getRegion();
    }

    public boolean hasIntersectionId() {
        return intersectionId != null;
    }


}
