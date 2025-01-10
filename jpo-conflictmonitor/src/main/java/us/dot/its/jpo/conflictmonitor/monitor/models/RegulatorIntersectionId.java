package us.dot.its.jpo.conflictmonitor.monitor.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode()
@ToString
public class RegulatorIntersectionId implements Comparable<RegulatorIntersectionId>{
    int roadRegulatorId = -1;
    int intersectionId = -1;

    public void setRoadRegulatorId(Integer roadRegulatorId){
        if(roadRegulatorId != null){
            this.roadRegulatorId = roadRegulatorId;
        } else{
            this.roadRegulatorId = -1;
        }
    }

    public void setIntersectionId(Integer intersectionId){
        if(intersectionId != null){
            this.intersectionId = intersectionId;
        } else{
            this.intersectionId = -1;
        }
    }

    @Override
    public int compareTo(RegulatorIntersectionId other) {
        if (other == null) return 1;
        if (this.roadRegulatorId != other.roadRegulatorId) {
            return Integer.compare(this.roadRegulatorId, other.roadRegulatorId);
        }
        return Integer.compare(this.intersectionId, other.intersectionId);
    }
}
