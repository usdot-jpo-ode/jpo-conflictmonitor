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

    private final ZonedDateTime notificationGeneratedAt = ZonedDateTime.now();
    public final String notificationType;

    public Notification(String notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * When the notification expires.
     */
    private ZonedDateTime notificationExpiresAt;

    /**
     * @return A string that uniquely identifies the notification 
     * for purposes of supressing duplicates.  It should 
     * not depend on the absolute values any timestamp fields.
     */
    public abstract String uniqueId();
}
