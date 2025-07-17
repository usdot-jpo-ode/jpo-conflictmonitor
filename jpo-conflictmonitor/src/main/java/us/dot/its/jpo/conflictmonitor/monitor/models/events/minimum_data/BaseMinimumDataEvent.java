package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

import java.util.List;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;

/**
 * Abstract class to hold data for minimum data events
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Generated
public abstract class BaseMinimumDataEvent extends Event  {

    public BaseMinimumDataEvent(String eventType) {
        super(eventType);
    }
    
    /**
     * The source RSU device ID
     */
    private String source;


    /**
     * The message processing time period
     */
    private ProcessingTimePeriod timePeriod;

    /**
     * CTI 4501 required elements that are missing
     */
    private List<String> missingDataElements;

}
