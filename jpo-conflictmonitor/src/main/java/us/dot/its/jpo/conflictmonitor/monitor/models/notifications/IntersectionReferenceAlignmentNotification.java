package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;

/**
 * Notification representing an intersection reference alignment event.
 * Contains the associated event and provides a unique identifier for the notification.
 */
public class IntersectionReferenceAlignmentNotification extends Notification {

    /**
     * Constructs an IntersectionReferenceAlignmentNotification with the notification type set.
     */
    public IntersectionReferenceAlignmentNotification() {
        super("IntersectionReferenceAlignmentNotification");
    }

    /** The associated IntersectionReferenceAlignmentEvent for this notification. */
    @Getter 
    private IntersectionReferenceAlignmentEvent event;

    /**
     * Sets the event for this notification and updates intersection and regulator IDs.
     *
     * @param event the IntersectionReferenceAlignmentEvent to associate with this notification
     */
    public void setEvent(IntersectionReferenceAlignmentEvent event){
        if(event != null){
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    /**
     * Returns a unique identifier for this notification, based on type, regulator ID, and intersection ID.
     *
     * @return unique identifier string for this notification
     */
    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s", 
            this.getNotificationType(), 
            event.getRoadRegulatorID(),
            event.getIntersectionID()
            );
    }
}