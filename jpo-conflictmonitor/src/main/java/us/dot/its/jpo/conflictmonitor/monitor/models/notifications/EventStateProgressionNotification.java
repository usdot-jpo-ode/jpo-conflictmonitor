package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEvent;

/**
 * Notification representing an event state progression anomaly.
 * Contains the associated event and provides a unique identifier for the notification.
 */
@Getter
@Setter
public class EventStateProgressionNotification extends Notification {

    /**
     * Constructs an EventStateProgressionNotification with the notification type set.
     */
    public EventStateProgressionNotification() {
        super("EventStateProgressionNotification");
    }

    /** The associated EventStateProgressionEvent for this notification. */
    EventStateProgressionEvent event;

    /**
     * Sets the event for this notification and updates intersection and regulator IDs,
     * as well as notification heading and text.
     *
     * @param event the EventStateProgressionEvent to associate with this notification
     */
    public void setEvent(EventStateProgressionEvent event) {
        if (event != null) {
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
            this.setNotificationHeading("Event State Progression");
            this.setNotificationText("An illegal SPaT transition was detected");
        }
    }

    /**
     * Returns a unique identifier for this notification, based on type, source, regulator ID, intersection ID, and signal group ID.
     *
     * @return unique identifier string for this notification
     */
    @Override
    public String getUniqueId() {
        if (event != null) {
            return String.format("%s_%s_%s_%s_%s",
                    this.getNotificationType(),
                    event.getSource(),
                    event.getRoadRegulatorID(),
                    event.getIntersectionID(),
                    event.getSignalGroupID());
        } else {
            return this.getNotificationType();
        }
    }
}
