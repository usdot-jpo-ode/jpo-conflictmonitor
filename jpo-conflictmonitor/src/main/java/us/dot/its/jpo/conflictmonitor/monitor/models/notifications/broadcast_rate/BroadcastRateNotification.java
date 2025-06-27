package us.dot.its.jpo.conflictmonitor.monitor.models.notifications.broadcast_rate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.BroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;

/**
 * Abstract base class for broadcast rate notifications.
 * <p>
 * Broadcast rate notifications represent events related to the rate at which messages are broadcast.
 * This class is intended to be extended by specific broadcast rate notification types.
 *
 * @param <T> the type of BroadcastRateEvent associated with this notification
 */
public abstract class BroadcastRateNotification<T extends BroadcastRateEvent> extends Notification {

    /**
     * Constructs a BroadcastRateNotification with the specified notification type.
     *
     * @param notificationType the type of notification
     */
    public BroadcastRateNotification(String notificationType) {
        super(notificationType);
    }

    /** The broadcast rate event associated with this notification. */
    @Getter @Setter private T event;
    
    /**
     * Returns a unique identifier for this notification, based on type, source, intersection ID,
     * time period (in milliseconds), and number of messages.
     *
     * @return unique identifier string for this notification
     */
    @Override
    @JsonIgnore
    public String getUniqueId() {
        return String.format("%s_%s_%s_%s_%s", 
            this.getNotificationType(), 
            event.getSource(), 
            event.getIntersectionID(), 
            event.getTimePeriod() != null ? event.getTimePeriod().periodMillis() : 0L,
            event.getNumberOfMessages());
    }
}
