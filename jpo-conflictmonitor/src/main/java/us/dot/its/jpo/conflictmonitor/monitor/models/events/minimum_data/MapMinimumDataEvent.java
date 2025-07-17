package us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data;

/**
 * MapMinimumDataEvent class. A MapMinimumDataEvent is generated whenever the CIMMS system receives an invalid number of MAP messages for the given analysis period. 
 * Typically this is generated if &lt; 9 or &gt; 11 MAP messages are received within an 10 second window.
 */
public class MapMinimumDataEvent extends BaseMinimumDataEvent {

    public MapMinimumDataEvent() {
        super("MapMinimumData");
    }
}
