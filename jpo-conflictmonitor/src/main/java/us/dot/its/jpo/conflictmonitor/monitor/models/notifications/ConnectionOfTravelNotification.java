package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;



public class ConnectionOfTravelNotification extends Notification {
    public ConnectionOfTravelNotification() {
        super("ConnectionOfTravelNotification");
    }

    @Getter private ConnectionOfTravelAssessment assessment;

    public void setAssessment(ConnectionOfTravelAssessment assessment){
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
            this.getIntersectionID(),
            this.getRoadRegulatorID()
        );
    }
}