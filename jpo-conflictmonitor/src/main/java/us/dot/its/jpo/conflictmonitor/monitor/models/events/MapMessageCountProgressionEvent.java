package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
* MapMessageCountProgressionEvent - This event type is generated when the message count of the MAP message is not incremented properly. 
* Each time the MAP message is updated (except for the timestamp) the messageCount of the MAP message should be incremented. If the message count is the max value of 127, it should roll over to 0.
* If the MAP message is updated, but the message count is not incremented or if the message count increments without a change in the MAP message this event will be generated.
*/
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class MapMessageCountProgressionEvent extends Event{

    /**
    *  String representing the source of the MAP message. Typically the intersectionID or RSU IP address
    */
    private String source;

    /**
    *  String representing the message type to be compatible with the BSM and SPaT variants of this event. Should be set to the value of MAP.
    */
    private String messageType;

    /**
    *  int representing the value of the message count field from the first MAP message
    */
    private int messageCountA;

    /**
    *  String representing a human readable timestamp from the first MAP message
    */
    private String timestampA;

    /**
    *  double representing the latitude of the first lane point
    */
    private int messageCountB;

    /**
    *  String representing a human readable timestamp from the second MAP message
    */
    private String timestampB;

    public MapMessageCountProgressionEvent(){
        super("MapMessageCountProgression");
    }

}