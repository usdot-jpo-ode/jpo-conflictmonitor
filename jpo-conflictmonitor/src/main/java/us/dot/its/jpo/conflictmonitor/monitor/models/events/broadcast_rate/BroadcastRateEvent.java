package us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate;


import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;

/**
 * Base class for a Broadcast Rate event to report counts of events during the processing time period.
 */
@Data
@Generated
public abstract class BroadcastRateEvent {
        
    /**
     * The name of the Kafka topic containing messages to be counted
     */
    private String topicName;

     /**
     * The source RSU Device ID
     */
    private String sourceDeviceId;

    /**
     * The message processing time period.
     */
    private ProcessingTimePeriod timePeriod;

    /**
     * @return The number of messages processed
     */
    private int numberOfMessages;
    
}
