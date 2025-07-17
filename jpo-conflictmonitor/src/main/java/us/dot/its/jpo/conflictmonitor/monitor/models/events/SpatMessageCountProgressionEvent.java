package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
* SpatMessageCountProgressionEvent - This event type is generated when the message count of the SPaT message is not incremented properly. 
* Each time the SPaT message is updated (except for the timestamp) the messageCount of the SPaT message should be incremented. If the message count is the max value of 127, it should roll over to 0.
* If the SPaT message is updated, but the message count is not incremented or if the message count increments without a change in the SPaT message this event will be generated.
*/
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class SpatMessageCountProgressionEvent extends Event{

    /**
    *  String representing the source of the MAP message. Typically the intersectionID or RSU IP address
    */
    private String source;

    /**
    *  String representing the message type to be compatible with the BSM and SPaT variants of this event. Should be set to the value of SPAT.
    */
    private String messageType;

    /**
    *  int representing the value of the message count field from the first SPaT message
    */
    private int messageCountA;

    /**
    *  String representing a human readable timestamp from the first SPaT message
    */
    private String timestampA;

    /**
    *  int representing the value of the message count field from the second SPaT message
    */
    private int messageCountB;

    /**
    *  String representing a human readable timestamp from the first SPaT message
    */
    private String timestampB;

    public SpatMessageCountProgressionEvent(){
        super("SpatMessageCountProgression");
    }

}
