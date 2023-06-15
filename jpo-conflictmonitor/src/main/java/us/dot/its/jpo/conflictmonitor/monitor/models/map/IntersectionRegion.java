package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import lombok.*;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

@Getter
@Setter
@EqualsAndHashCode
public class IntersectionRegion {

    public IntersectionRegion() {}

    public IntersectionRegion(Integer intersectionId, Integer region) {
        this.intersectionId = intersectionId;
        this.region = region;
    }

    public IntersectionRegion(ProcessedMap map) {
        if (map == null || map.getProperties() == null) return;
        this.intersectionId = map.getProperties().getIntersectionId();
        this.region = map.getProperties().getRegion();
    }

    private Integer intersectionId;
    private Integer region;


}
