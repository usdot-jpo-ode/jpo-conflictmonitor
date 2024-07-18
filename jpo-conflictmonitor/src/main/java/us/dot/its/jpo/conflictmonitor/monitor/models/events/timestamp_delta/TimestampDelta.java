package us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

@Getter
@Setter
@EqualsAndHashCode
@Generated
@Slf4j
public class TimestampDelta {

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

    public boolean emitEvent() {
        return Math.abs(actualDeltaMilliseconds()) > maxDeltaMilliseconds;
    }

    @Override
    public String toString() {
        try {
            return DateJsonMapper.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Exception serializing TimestampDelta Event to JSON", e);
        }
        return "";
    }
}
