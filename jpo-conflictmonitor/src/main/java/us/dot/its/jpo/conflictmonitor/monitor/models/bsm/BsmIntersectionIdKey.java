package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BsmIntersectionIdKey extends RsuIntersectionKey {

    String bsmId;

    /** 
     * Creates a new Empty BsmIntersectionIdKey
     */
    public BsmIntersectionIdKey() {}

    /** 
     * @param bsmid The vehicle ID of the BSM.
     * @param rsuId The IP address of the RSU to use
     * @param intersectionId The intersection ID of a BSM inside of an intersection. Use -1 if vehicle is not inside of an intersection.
     * @param intersectionId the intersection Id the BSM was inside. Use -1 if BSM is not in an intersection
     */
    public BsmIntersectionIdKey(String bsmId, String rsuId, int intersectionId) {
        super(rsuId, intersectionId);
        this.bsmId = bsmId;
    }

    /** 
     * @param bsmid The vehicle ID of the BSM.
     * @param rsuId The IP address of the RSU to use
     * @param intersectionId The intersection ID of a BSM inside of an intersection. Use -1 if vehicle is not inside of an intersection.
     * @param region The region a BSM is inside of. Regions are gradually being deprecated. Use Alternate constructor without region instead.
     */
    @Deprecated
    public BsmIntersectionIdKey(String bsmId, String rsuId, int intersectionId, int region) {
        super(rsuId, intersectionId, region);
        this.bsmId = bsmId;
    }

    /** 
     * @return An IntersectionRegion object including the Intersection and Region of this Key.
     */
    @JsonIgnore
    public IntersectionRegion getIntersectionRegion() {
        return new IntersectionRegion(getIntersectionId(), getRegion());
    }

}
