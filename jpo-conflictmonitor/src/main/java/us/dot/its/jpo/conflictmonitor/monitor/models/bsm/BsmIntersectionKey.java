package us.dot.its.jpo.conflictmonitor.monitor.models.bsm;

import java.util.Objects;

import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;

public class BsmIntersectionKey implements RsuIdKey{

    private String rsuId;

    public BsmIntersectionKey(String rsuId){
        this.rsuId = rsuId;
    }

    @Override
    public String getRsuId() {
        return this.rsuId;
    }

    public void setRsuId(String rsuId) {
        this.rsuId = rsuId;
    }

     @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BsmIntersectionKey)) {
            return false;
        }
        BsmIntersectionKey bsmIntersectionKey = (BsmIntersectionKey) o;
        return Objects.equals(rsuId, bsmIntersectionKey.rsuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rsuId);
    }
    



    @Override
    public String toString() {
        return "{" +
            " rsuId='" + getRsuId() + "'" +
            "}";
    }
    


    
}
