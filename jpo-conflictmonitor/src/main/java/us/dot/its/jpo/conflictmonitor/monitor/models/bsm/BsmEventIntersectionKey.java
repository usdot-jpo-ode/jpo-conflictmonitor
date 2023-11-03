//package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.*;
//import us.dot.its.jpo.conflictmonitor.monitor.models.map.IntersectionRegion;
//
//
//@Getter
//@Setter
//@EqualsAndHashCode(callSuper = true)
//@Generated
//public class BsmEventIntersectionKey extends BsmRsuIdKey {
//
//    public BsmEventIntersectionKey() {}
//
//    public BsmEventIntersectionKey(BsmRsuIdKey superKey) {
//        super(superKey.getRsuId(), superKey.getBsmId());
//    }
//
//    public BsmEventIntersectionKey(BsmRsuIdKey superKey, IntersectionRegion intersectionRegion) {
//        super(superKey.getRsuId(), superKey.getBsmId());
//        setIntersectionRegion(intersectionRegion);
//    }
//
//    private Integer intersectionId;
//    private Integer region;
//
//    @JsonIgnore
//    public BsmRsuIdKey getBsmIntersectionKey() {
//        return new BsmRsuIdKey(getRsuId(), getBsmId());
//    }
//
//    @JsonIgnore
//    public IntersectionRegion getIntersectionRegion() {
//        return new IntersectionRegion(intersectionId, region);
//    }
//
//    @JsonIgnore
//    public void setIntersectionRegion(IntersectionRegion intersectionRegion) {
//        this.intersectionId = intersectionRegion.getIntersectionId();
//        this.region = intersectionRegion.getRegion();
//    }
//
//    public boolean hasIntersectionId() {
//        return intersectionId != null;
//    }
//
//
//}
