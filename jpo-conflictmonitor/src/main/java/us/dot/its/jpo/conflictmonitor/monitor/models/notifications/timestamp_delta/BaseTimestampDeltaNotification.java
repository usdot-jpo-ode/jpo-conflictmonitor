package us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.BaseTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;

import java.util.List;

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
    int numberOfEvents;
    TimestampDelta minDelta;
    TimestampDelta maxDelta;
    TimestampDelta meanDelta;

    @Override
    public String getUniqueId() {
        return String.format("%s_%d_%d_%d_%d_%d_%d",
                this.getNotificationType(),
                this.getRoadRegulatorID(),
                this.getIntersectionID(),
                numberOfEvents,
                minDelta != null ? minDelta.getDeltaMilliseconds(): 0L,
                maxDelta != null ? maxDelta.getDeltaMilliseconds() : 0L,
                meanDelta != null ? meanDelta.getDeltaMilliseconds() : 0L);
    }
}
