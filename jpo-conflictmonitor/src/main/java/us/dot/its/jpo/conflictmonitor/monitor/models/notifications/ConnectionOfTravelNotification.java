package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmConnectionOfTravelNotification")
public class ConnectionOfTravelNotification extends Notification {
    public ConnectionOfTravelNotification() {
        super("ConnectionOfTravelNotification");
    }

    @Getter private ConnectionOfTravelAssessment assessment;

    public void setAssessment(ConnectionOfTravelAssessment assessment){
        if(assessment != null){
            this.assessment = assessment;
            this.key = getUniqueId();
        }
    }

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s", 
            this.getNotificationType(), 
            assessment.getAssessmentType()
        );
    }
}