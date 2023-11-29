package us.dot.its.jpo.conflictmonitor.monitor.models.map;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
public class IntersectionRegion {

    public IntersectionRegion() {}

    public IntersectionRegion(int intersectionId, int region) {
        this.intersectionId = intersectionId;
        this.region = region;
    }


    private int intersectionId;
    private int region;


}
