package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;



public class ConnectionOfTravelNotification extends Notification {
    public ConnectionOfTravelNotification() {
        super("ConnectionOfTravelNotification");
    }

    @Getter @Setter private ConnectionOfTravelAssessment assessment;

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s", 
            this.getNotificationType(), 
            assessment.getAssessmentType()
        );
    }
}