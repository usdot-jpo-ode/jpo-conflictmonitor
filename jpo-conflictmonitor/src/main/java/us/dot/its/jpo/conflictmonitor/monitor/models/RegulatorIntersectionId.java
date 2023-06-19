package us.dot.its.jpo.conflictmonitor.monitor.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode()
public class RegulatorIntersectionId {
    Integer roadRegulatorId;
    Integer intersectionId;

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

}
