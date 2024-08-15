package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.SpatTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.SpatTimestampDeltaNotification;

import java.time.Duration;

@Slf4j
public class SpatTimestampDeltaNotificationProcessor
    extends BaseTimestampDeltaNotificationProcessor<SpatTimestampDeltaEvent, SpatTimestampDeltaNotification> {

    public SpatTimestampDeltaNotificationProcessor(Duration retentionTime, String eventStoreName, String keyStoreName) {
        super(retentionTime, eventStoreName, keyStoreName);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected SpatTimestampDeltaNotification constructNotification() {
        return new SpatTimestampDeltaNotification();
    }

    @Override
    protected String getNotificationHeading() {
        return "SPaT Timestamp Delta Notification";
    }

    @Override
    protected String getNotificationText() {
        return "There were differences between the ODE ingest time and message timestamp.";
    }
}
