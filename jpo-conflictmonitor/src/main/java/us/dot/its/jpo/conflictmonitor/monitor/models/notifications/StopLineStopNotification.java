package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessment;


import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmStopLineStopNotification")
public class StopLineStopNotification extends Notification {
    public StopLineStopNotification() {
        super("StopLineStopNotification");
    }

    
    @Getter private StopLineStopAssessment assessment;
    
    public void setAssessment(StopLineStopAssessment assessment){
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