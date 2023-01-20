package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.Generated;

/**
 * Base class for Notification messages.
 * 
 * <p>Notifications are informational messages that signal
 * an anomaly or error condition.
 */
@Data
@Generated
public abstract class Notification {

    private final long notificationGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    public final String notificationType;
    public String notificationText;
    public String notificationHeading;

    public Notification(String notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * When the notification expires.
     */
    private ZonedDateTime notificationExpiresAt;

    /**
     * @return A string that uniquely identifies the notification 
     * for purposes of suppressing duplicates within the Conflict Monitor.  
     * It should not depend on the absolute values of any timestamp fields.
     */
    public abstract String uniqueId();

    
}
