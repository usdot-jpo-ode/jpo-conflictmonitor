package us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;

@Getter
@Setter
@Generated
public abstract class KafkaStreamsEvent extends Event {
    public KafkaStreamsEvent(String eventType) {
        super(eventType);
    }

        /**
     * Identifier for the instance of this application that produced this event,
     * for example host name or pod IP.
     */
    public String appId;

    /**
     * The name of the topology that changed state.
     */
    public String topology;
}
