package us.dot.its.jpo.conflictmonitor.monitor.models.notifications.app_health;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health.KafkaStreamsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health.KafkaStreamsStateChangeEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health.KafkaStreamsUnhandledExceptionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;

@Getter
@Setter
public class KafkaStreamsAnomalyNotification extends Notification {

    KafkaStreamsStateChangeEvent stateChange;
    KafkaStreamsUnhandledExceptionEvent exceptionEvent;

    public KafkaStreamsAnomalyNotification() {
        super("AppHealthNotification");
    }

    @Override
    public String getUniqueId() {
        if (stateChange != null) return generateUniqueId(stateChange);
        if (exceptionEvent != null) return generateUniqueId(exceptionEvent);
        return "null";
    }
    
    private String generateUniqueId(KafkaStreamsEvent event) {
        return String.format("%s_%s_%s",
        event.getEventType(),
        event.getAppId(),
        event.getTopology());
    }
}
