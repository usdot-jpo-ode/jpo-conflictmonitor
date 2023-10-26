package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessment;


import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmStopLinePassageNotification")
public class StopLinePassageNotification extends Notification {
    public StopLinePassageNotification() {
        super("StopLinePassageNotification");
    }

    
    @Getter private StopLinePassageAssessment assessment;
    
    public void setAssessment(StopLinePassageAssessment assessment){
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