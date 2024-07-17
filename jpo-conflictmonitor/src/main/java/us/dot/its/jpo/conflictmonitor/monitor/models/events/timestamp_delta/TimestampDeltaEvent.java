package us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public class TimestampDeltaEvent extends Event {

    public TimestampDeltaEvent() {
        super("TimestampDeltaEvent");
    }

    /**
     * The name of the input topic
     */
    String topicName;

    /**
     * The source RSU device ID
     */
    String source;

    /**
     * The timestamp difference
     */
    TimestampDelta delta;


}
