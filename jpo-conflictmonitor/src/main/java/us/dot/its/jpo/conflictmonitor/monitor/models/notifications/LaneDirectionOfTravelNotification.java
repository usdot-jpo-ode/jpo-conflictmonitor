package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessment;

/**
 * Notification representing a lane direction of travel assessment event.
 * Contains the associated assessment and provides a unique identifier for the notification.
 */
public class LaneDirectionOfTravelNotification extends Notification {

    /**
     * Constructs a LaneDirectionOfTravelNotification with the notification type set.
     */
    public LaneDirectionOfTravelNotification() {
        super("LaneDirectionOfTravelAssessmentNotification");
    }

    /** The assessment associated with this notification. */
    @Getter 
    private LaneDirectionOfTravelAssessment assessment;

    /** The lane ID associated with this notification. */
    @Getter @Setter 
    private int laneId;

    /** The segment ID associated with this notification. */
    @Getter @Setter 
    private int segmentId;

    /**
     * Sets the assessment for this notification and updates intersection and regulator IDs.
     *
     * @param assessment the LaneDirectionOfTravelAssessment to associate with this notification
     */
    public void setAssessment(LaneDirectionOfTravelAssessment assessment){
        if(assessment != null){
            this.assessment = assessment;
            this.setIntersectionID(assessment.getIntersectionID());
            this.setRoadRegulatorID(assessment.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    /**
     * Returns a unique identifier for this notification, based on type, intersection ID, regulator ID, lane ID, and segment ID.
     *
     * @return unique identifier string for this notification
     */
    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s_%s", 
            this.getNotificationType(), 
            assessment.getIntersectionID(),
            assessment.getRoadRegulatorID(),
            this.getLaneId(),
            this.getSegmentId()
        );
    }
}