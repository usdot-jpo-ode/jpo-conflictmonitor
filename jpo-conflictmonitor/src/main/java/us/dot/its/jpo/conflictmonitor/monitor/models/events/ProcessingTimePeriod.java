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
    private long beginTimestamp;

    /**
     * The timestamp at the end of the processing period in epoch milliseconds
     */
    private long endTimestamp;

    
  
    public long periodMillis() {
        return Math.abs(endTimestamp - beginTimestamp); 
    }

}
