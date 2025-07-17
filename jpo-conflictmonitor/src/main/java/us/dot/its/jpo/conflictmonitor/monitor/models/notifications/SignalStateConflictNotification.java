package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;

/**
 * Notification representing a signal state conflict event.
 * Contains the associated event and provides a unique identifier for the notification.
 */
public class SignalStateConflictNotification extends Notification {

    /**
     * Constructs a SignalStateConflictNotification with the notification type set.
     */
    public SignalStateConflictNotification() {
        super("SignalStateConflictNotification");
    }

    /** The associated SignalStateConflictEvent for this notification. */
    @Getter 
    private SignalStateConflictEvent event;

    /**
     * Sets the event for this notification and updates intersection and regulator IDs.
     *
     * @param event the SignalStateConflictEvent to associate with this notification
     */
    public void setEvent(SignalStateConflictEvent event){
        if(event != null){
            this.event = event;
            this.setIntersectionID(event.getIntersectionID());
            this.setRoadRegulatorID(event.getRoadRegulatorID());
            this.key = getUniqueId();
        }
    }

    /**
     * Returns a unique identifier for this notification, based on type, conflicting signal groups, intersection ID, and regulator ID.
     *
     * @return unique identifier string for this notification
     */
    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s_%s", 
            this.getNotificationType(),
            event.getFirstConflictingSignalGroup(),
            event.getSecondConflictingSignalGroup(),
            event.getIntersectionID(),
            event.getRoadRegulatorID()
        );
    }
}