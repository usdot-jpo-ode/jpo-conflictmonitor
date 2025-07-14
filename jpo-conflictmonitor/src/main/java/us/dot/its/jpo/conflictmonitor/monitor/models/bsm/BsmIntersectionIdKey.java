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

    public BsmIntersectionIdKey() {
    }

    /**
     * @param bsmId          The vehicle ID of the BSM.
     * @param rsuId          The IP address of the RSU to use
     * @param intersectionId The intersection ID of a BSM inside of an intersection.
     *                       Use -1 if vehicle is not inside of an intersection.
     * @param logId          The Logfile ID the BSM came from. Used for BSM's uploaded via log files.
     */
    public BsmIntersectionIdKey(String bsmId, String rsuId, int intersectionId, String logId) {
        super(rsuId, intersectionId);
        this.bsmId = bsmId;
        this.logId = logId;
    }

    /**
     * @param bsmId          The vehicle ID of the BSM.
     * @param rsuId          The IP address of the RSU to use
     * @param intersectionId The intersection ID of a BSM inside of an intersection.
     *                       Use -1 if vehicle is not inside of an intersection.
     * @param region         The region a BSM is inside of. Regions are gradually
     *                       being deprecated. Use Alternate constructor without
     *                       region instead.
     */
    @Deprecated
    public BsmIntersectionIdKey(String bsmId, String rsuId, int intersectionId, int region, String logId) {
        super(rsuId, intersectionId, region);
        this.bsmId = bsmId;
        this.logId = logId;
    }

    /**
     * @return An IntersectionRegion object including the Intersection and Region of
     *         this Key.
     */
    @JsonIgnore
    public IntersectionRegion getIntersectionRegion() {
        return new IntersectionRegion(getIntersectionId(), getRegion());
    }

}
