package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
 * BsmMessageCountProgressionEvents are generated when multiple BSM's for the same vehicle are not tracked sequentially in the CIMMS system. 
 * This may occur if the vehicle broadcasting the BSM messages is not properly incrementing the message count in the BSM message with each subsequent message, or if messages are being received out of order.
 * This event may also be generated if a vehicle doesn't properly roll over the BSM message counter when it reaches the maximum value.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class BsmMessageCountProgressionEvent extends Event{

    /**
     * String representing the source of the BSM message. Vehicle source typically includes the vehicle ID and the IP of the RSU which received the BSM message.
     */
    private String source;

    /**
     * String representing that this event was generated for a BSM message. Typically set to the value of BSM. Used to ensure compatibility with SPaT and MAP message count progression events.
     */
    private String messageType;

    /**
     * int representing the message count of the first BSM used to compare multiple BSM messages. This value should be 1 less than the value in messageCountB
     */
    private int messageCountA;

    /**
     * Human readable timestamp of the first BSM message
     */
    private String timestampA;

    /**
     * int representing the message count of the second BSM used in the comparison. This value should be 1 more than the value in messageCountA. If messageCountA is 127, this value may be 0.
     */
    private int messageCountB;

    /**
     * Human readable timestamp of the second BSM message
     */
    private String timestampB;

    /**
     * String representing the vehicleId of the BSM's being compared.
     */
    private String vehicleId;

    public BsmMessageCountProgressionEvent(){
        super("BsmMessageCountProgression");
    }

}