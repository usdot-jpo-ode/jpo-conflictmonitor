package us.dot.its.jpo.conflictmonitor.monitor.models.events;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

/**
 * ConnectionOfTravelEvent class - Connection of Travel events are generated whenever a vehicle drives through an intersection. 
 * Connection of travel events are used to indicate which ingress and egress lanes a vehicle takes through the intersection. 
 * If the vehicle enters or exits an intersection using an invalid lane, a value of -1 is used for the missing ingres or egress lane. 
 * This can be used to indicate if vehicles frequently take invalid lanes into or out of the intersection
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class ConnectionOfTravelEvent extends Event{
    /**
     * The time at which the vehicle drove through the intersection. Taken from the BSM messages for the vehicle
     */
    private long timestamp;

    /**
     * int representing the lane ID of the ingress lane the vehicle took when entering the intersection. 
     */
    private int ingressLaneID;

    /**
     * int representing the lane ID of the egress lane the vehicle took when entering the intersection. 
     */
    private int egressLaneID;

    /**
     * String source of the Connection of Travel Event. Typically left blank - Consider deprecating and removal in future release.
     */
    private String source;

    /**
     * <p>The array index of the connecting lane feature in the 'connectingLanesFeatureConnection' property
     * of the {@link us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap}, or -1 if there is no
     * connection between the ingress and egress.
     *
     * <p>Note this property DOES NOT equal the J2735 ConnectionID from the raw MAP message.
     */
    private int connectionID;

    public ConnectionOfTravelEvent(){
        super("ConnectionOfTravel");
    }

    @JsonIgnore
    public String getKey(){
        return this.getIntersectionID() + "_" + this.getIngressLaneID() + "_" + this.getEgressLaneID();
    }
}
