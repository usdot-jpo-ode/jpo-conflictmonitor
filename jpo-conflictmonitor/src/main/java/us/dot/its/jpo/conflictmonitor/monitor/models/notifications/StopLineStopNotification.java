package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessment;

/**
 * Notification representing a stop event at a stop line.
 * Contains the associated assessment and provides a unique identifier for the notification.
 */
@Getter 
@Setter
public class StopLineStopNotification extends Notification {

    /**
     * Constructs a StopLineStopNotification with the notification type set.
     */
    public StopLineStopNotification() {
        super("StopLineStopNotification");
    }

    /** The signal group associated with this stop notification. */
    private int signalGroup;

    /** The assessment associated with this stop notification. */
    @Getter 
    private StopLineStopAssessment assessment;

    /**
     * Sets the assessment for this notification and updates intersection and regulator IDs.
     *
     * @param assessment the StopLineStopAssessment to associate with this notification
     */
    public void setAssessment(StopLineStopAssessment assessment){
        if(assessment != null){
            this.assessment = assessment;
            this.setIntersectionID(assessment.getIntersectionID());
            this.setRoadRegulatorID(assessment.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    /**
     * Returns a unique identifier for this notification, based on type, intersection ID, regulator ID, and signal group.
     *
     * @return unique identifier string for this notification
     */
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