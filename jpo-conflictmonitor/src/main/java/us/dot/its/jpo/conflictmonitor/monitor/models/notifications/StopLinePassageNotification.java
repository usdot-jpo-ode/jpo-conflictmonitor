package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessment;


@Getter
@Setter
public class StopLinePassageNotification extends Notification {
    public StopLinePassageNotification() {
        super("StopLinePassageNotification");
    }

    private int signalGroup;

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
        return String.format("%s_%s_%s_%s", 
            this.getNotificationType(), 
            assessment.getIntersectionID(),
            assessment.getRoadRegulatorID(),
            this.getSignalGroup()
        );
    }
}