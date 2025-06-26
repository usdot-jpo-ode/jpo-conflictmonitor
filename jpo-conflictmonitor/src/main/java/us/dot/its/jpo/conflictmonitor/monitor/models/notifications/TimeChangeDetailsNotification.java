package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;

/**
 * Notification representing a time change details event.
 * Contains the event data and provides a unique identifier for the notification.
 */
public class TimeChangeDetailsNotification extends Notification {

    /**
     * Constructs a TimeChangeDetailsNotification with the notification type set.
     */
    public TimeChangeDetailsNotification() {
        super("TimeChangeDetailsNotification");
    }

    /** The associated TimeChangeDetailsEvent for this notification. */
    @Getter
    private TimeChangeDetailsEvent event;

    /**
     * Sets the event for this notification and updates intersection and regulator IDs.
     *
     * @param event the TimeChangeDetailsEvent to associate with this notification
     */
    public void setEvent(TimeChangeDetailsEvent event){
        if(event != null){
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    /**
     * Returns a unique identifier for this notification, based on type, regulator ID, intersection ID, and signal group.
     *
     * @return unique identifier string for this notification
     */
    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s", 
            this.getNotificationType(),
            event.getRoadRegulatorID(),
            event.getIntersectionID(),
            event.getSignalGroup()          
        );
    }
}