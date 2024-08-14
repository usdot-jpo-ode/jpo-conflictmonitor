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
    int maxDeltaMillis;

    public void setMaxDeltaMillis(int maxDeltaMillis) {
        this.maxDeltaMillis = Math.abs(maxDeltaMillis);
    }

    /**
     * The ODE ingest timestamp
     */
    long odeIngestTimestampMillis;

    /**
     * The timestamp extracted from the message
     */
    long messageTimestampMillis;

    /**
     * @return The actual timestamp delta, signed
     */
    public long getDeltaMillis() {
        return odeIngestTimestampMillis - messageTimestampMillis;
    }

    /**
     * @return The magnitude of the delta, always positive.
     */
    public long getAbsDeltaMillis() {
        return Math.abs(getDeltaMillis());
    }

    public boolean emitEvent() {
        // If OdeReceivedAt is earlier than message timestamp, always emit an event
        // otherwise emit one if the lag delta exceeds the max
        return isOdeIngestBeforeMessageTimestamp() || isDeltaGreaterThanMax();
    }

    public boolean isOdeIngestBeforeMessageTimestamp() {
        return getDeltaMillis() < 0;
    }

    public boolean isDeltaGreaterThanMax() {
        return getAbsDeltaMillis() > maxDeltaMillis;
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
