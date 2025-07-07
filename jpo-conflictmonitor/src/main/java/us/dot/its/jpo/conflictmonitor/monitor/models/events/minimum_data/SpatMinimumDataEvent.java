package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

/**
 * SpatMinimumDataEvent class. A SpatMinimumDataEvent is generated whenever the CIMMS system receives an invalid number of SPaT messages for the given analysis period. 
 * Typically this is generated if &lt; 90 or &gt; 110 SPaT messages are received within an 10 second window.
 */
public class SpatMinimumDataEvent extends BaseMinimumDataEvent {

    public SpatMinimumDataEvent() {
        super("SpatMinimumData");
    }

}
