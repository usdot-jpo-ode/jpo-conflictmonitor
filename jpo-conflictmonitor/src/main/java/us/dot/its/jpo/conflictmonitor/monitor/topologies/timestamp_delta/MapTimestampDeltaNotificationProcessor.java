package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.MapTimestampDeltaNotification;

import java.time.Duration;

@Slf4j
public class MapTimestampDeltaNotificationProcessor
    extends BaseTimestampDeltaNotificationProcessor<MapTimestampDeltaEvent, MapTimestampDeltaNotification> {

    public MapTimestampDeltaNotificationProcessor(Duration retentionTime, String eventStoreName, String keyStoreName) {
        super(retentionTime, eventStoreName, keyStoreName);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected MapTimestampDeltaNotification constructNotification() {
        return new MapTimestampDeltaNotification();
    }

    @Override
    protected String getNotificationHeading() {
        return "MAP Timestamp Delta Notification";
    }

    @Override
    protected String getNotificationText() {
        return "There were differences between the ODE ingest time and message timestamp.";
    }
}
