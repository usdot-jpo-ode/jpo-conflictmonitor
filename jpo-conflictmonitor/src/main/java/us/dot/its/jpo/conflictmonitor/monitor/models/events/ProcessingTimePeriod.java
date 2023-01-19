package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.Generated;

/**
 * A processing time period with begin and end timestamps
 */
@Data
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

  

}
