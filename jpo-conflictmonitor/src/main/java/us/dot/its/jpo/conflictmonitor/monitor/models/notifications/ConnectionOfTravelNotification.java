package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;

/**
 * Notification representing a connection of travel assessment event.
 * Contains the associated assessment and provides a unique identifier for the notification.
 */
public class ConnectionOfTravelNotification extends Notification {

    /**
     * Constructs a ConnectionOfTravelNotification with the notification type set.
     */
    public ConnectionOfTravelNotification() {
        super("ConnectionOfTravelNotification");
    }

    /** The assessment associated with this notification. */
    @Getter 
    private ConnectionOfTravelAssessment assessment;
    
    /** The ingress lane ID associated with this notification. */
    @Getter 
    @Setter 
    private int ingressLane;
    
    /** The egress lane ID associated with this notification. */
    @Getter 
    @Setter 
    private int egressLane;

    /**
     * Sets the assessment for this notification and updates intersection and regulator IDs.
     *
     * @param assessment the ConnectionOfTravelAssessment to associate with this notification
     */
    public void setAssessment(ConnectionOfTravelAssessment assessment){
        if(assessment != null){
            this.assessment = assessment;
            this.setIntersectionID(assessment.getIntersectionID());
            this.setRoadRegulatorID(assessment.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    /**
     * Returns a unique identifier for this notification, based on type, intersection ID, regulator ID, ingress lane, and egress lane.
     *
     * @return unique identifier string for this notification
     */
    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s_%s", 
            this.getNotificationType(),
            this.getIntersectionID(),
            this.getRoadRegulatorID(),
            this.getIngressLane(),
            this.getEgressLane()
        );
    }
}