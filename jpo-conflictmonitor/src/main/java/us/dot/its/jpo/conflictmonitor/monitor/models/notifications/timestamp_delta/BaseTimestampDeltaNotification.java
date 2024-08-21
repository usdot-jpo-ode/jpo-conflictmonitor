package us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;

/**
 * Base class for a timestamp delta notification
 */
@Getter
@Setter
public abstract class BaseTimestampDeltaNotification extends Notification {

    public BaseTimestampDeltaNotification(String notificationType) {
        super(notificationType);
    }

    ProcessingTimePeriod timePeriod;
    long numberOfEvents;

    /**
     * Minimum delta in milliseconds, signed.
     * <p>If this is less than 0, indicates there were events with OdeReceivedAt earlier
     * than the message timestamp.
     */
    long minDeltaMillis;

    /**
     * Maximum delta in milliseconds, signed.
     */
    long maxDeltaMillis;

    /**
     * The median magnitude of the delta in milliseconds.
     */
    double absMedianDeltaMillis;


    @Override
    public String getUniqueId() {
        return String.format("%s_%d_%d_%d_%d",
                this.getNotificationType(),
                this.getRoadRegulatorID(),
                this.getIntersectionID(),
                getTimePeriod() != null ? getTimePeriod().getBeginTimestamp() : 0,
                getTimePeriod() != null ? getTimePeriod().getEndTimestamp() : 0);
    }
}
