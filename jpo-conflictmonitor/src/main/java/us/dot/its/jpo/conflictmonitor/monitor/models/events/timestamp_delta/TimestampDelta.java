package us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

@Getter
@Setter
@EqualsAndHashCode
@Generated
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimestampDelta {

    /**
     * The configured max delta (absolute value)
     */
    int maxDeltaMilliseconds;

    public void setMaxDeltaMilliseconds(int maxDeltaMilliseconds) {
        this.maxDeltaMilliseconds = Math.abs(maxDeltaMilliseconds);
    }

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
    public long getDeltaMilliseconds() {
        return odeIngestTimestampMilliseconds - messageTimestampMilliseconds;
    }

    public boolean emitEvent() {
        // If OdeReceivedAt is earlier than message timestamp, always emit an event
        // otherwise emit one if the lag delta exceeds the max
        return isOdeIngestBeforeMessageTimestamp() || isDeltaGreaterThanMax();
    }

    public boolean isOdeIngestBeforeMessageTimestamp() {
        return getDeltaMilliseconds() < 0;
    }

    public boolean isDeltaGreaterThanMax() {
        return Math.abs(getDeltaMilliseconds()) > maxDeltaMilliseconds;
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
