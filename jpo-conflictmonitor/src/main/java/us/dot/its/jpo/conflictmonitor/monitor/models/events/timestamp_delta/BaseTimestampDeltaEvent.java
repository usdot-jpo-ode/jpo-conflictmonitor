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
public abstract class BaseTimestampDeltaEvent extends Event {

    public BaseTimestampDeltaEvent(String inputType) {
        super("TimestampDeltaEvent");
        this.inputType = inputType;
    }

    final String inputType;

    /**
     * The source RSU device ID
     */
    String source;

    /**
     * The timestamp difference
     */
    TimestampDelta delta;



}
