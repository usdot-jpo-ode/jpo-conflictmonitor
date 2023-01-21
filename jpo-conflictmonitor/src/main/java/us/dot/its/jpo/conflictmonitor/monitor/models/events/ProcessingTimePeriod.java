package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;

/**
 * A processing time period with begin and end timestamps
 */
@Getter
@Setter
@EqualsAndHashCode
@Generated
public class ProcessingTimePeriod {
    
    /**
     * The timestamp at the beginning of the processing period in epoch milliseconds
     */
    private ZonedDateTime beginTimestamp;

    /**
     * The timestamp at the end of the processing period in epoch milliseconds
     */
    private ZonedDateTime endTimestamp;

    public long beginMillis() {
        if (beginTimestamp == null) return 0L;
        return beginTimestamp.toInstant().toEpochMilli();
    }

    public long endMillis() {
        if (endTimestamp == null) return 0L;
        return endTimestamp.toInstant().toEpochMilli();
    }
  
    public long periodMillis() {
        return Math.abs(endMillis() - beginMillis()); 
    }

}
