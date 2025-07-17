package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;

/**
 * Notification representing a signal group alignment event.
 * Contains the associated event and provides a unique identifier for the notification.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignalGroupAlignmentNotification extends Notification {

    /**
     * Constructs a SignalGroupAlignmentNotification with the notification type set.
     */
    public SignalGroupAlignmentNotification() {
        super("SignalGroupAlignmentNotification");
    }

    /** The associated SignalGroupAlignmentEvent for this notification. */
    @Getter 
    private SignalGroupAlignmentEvent event;

    /**
     * Sets the event for this notification and updates intersection and regulator IDs.
     *
     * @param event the SignalGroupAlignmentEvent to associate with this notification
     */
    public void setEvent(SignalGroupAlignmentEvent event){
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