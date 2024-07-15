package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

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
     * The configured max delta (absolute value)
     */
    int maxDeltaMilliseconds;

    /**
     * The ODE ingest timestamp
     */
    long odeIngestTimestampMilliseconds;

    /**
     * The timestamp extracted from the message
     */
    long messageTimestampMilliseconds;

    /**
     * The actual timestamp delta
     */
    public long actualDeltaMilliseconds() {
        return odeIngestTimestampMilliseconds - messageTimestampMilliseconds;
    }



}
