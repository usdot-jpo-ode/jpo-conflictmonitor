package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.EqualsAndHashCode;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;

@Data
@EqualsAndHashCode(callSuper = true)
@Generated
public abstract class MinimumDataEvent extends Event  {

    public MinimumDataEvent(String eventType) {
        super(eventType);
    }
    
    /**
     * The source RSU device ID
     */
    private String sourceDeviceId;

    /**
     * The intersection id
     */
    private int intersectionId;

    /**
     * The message processing time period
     */
    private ProcessingTimePeriod timePeriod;

    /**
     * CTI 4501 required elements that are missing
     */
    private List<String> missingDataElements;

}
