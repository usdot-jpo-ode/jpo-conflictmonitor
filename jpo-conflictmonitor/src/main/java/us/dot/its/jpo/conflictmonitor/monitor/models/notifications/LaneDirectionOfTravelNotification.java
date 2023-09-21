package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessment;



public class LaneDirectionOfTravelNotification extends Notification {
    public LaneDirectionOfTravelNotification() {
        super("LaneDirectionOfTravelAssessmentNotification");
    }

    
    @Getter private LaneDirectionOfTravelAssessment assessment;
    
    public void setAssessment(LaneDirectionOfTravelAssessment assessment){
        if(assessment != null){
            this.assessment = assessment;
            this.setIntersectionID(assessment.getIntersectionID());
            this.setRoadRegulatorID(assessment.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }


    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s", 
            this.getNotificationType(), 
            assessment.getIntersectionID(),
            assessment.getRoadRegulatorID()
        );
    }
}