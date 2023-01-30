package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessment;



public class LaneDirectionOfTravelNotification extends Notification {
    public LaneDirectionOfTravelNotification() {
        super("LaneDirectionOfTravelAssessmentNotification");
    }

    @Getter @Setter private LaneDirectionOfTravelAssessment assessment;

    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s", 
            this.getNotificationType(), 
            assessment.getAssessmentType(),
            assessment.getIntersectionID(),
            assessment.getRoadRegulatorID()
        );
    }
}