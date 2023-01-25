package us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;

/**
 * Base class for a Broadcast Rate event to report counts of events during the processing time period.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public abstract class BroadcastRateEvent extends Event {

    public BroadcastRateEvent(String eventType) {
        super(eventType);
    }
        
    /**
     * The name of the Kafka topic containing messages to be counted
     */
    private String topicName;

    /**
     * The source RSU Device ID
     */
    private String sourceDeviceId;

    /**
     * The intersection id
     */
    private int intersectionId;

    /**
     * The message processing time period.
     */
    private ProcessingTimePeriod timePeriod;

    /**
     * @return The number of messages processed
     */
    private int numberOfMessages;

    
}
