package us.dot.its.jpo.conflictmonitor.monitor.models.events;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class ConnectionOfTravelEvent extends Event{
    private long timestamp;
    private int ingressLaneID;
    private int egressLaneID;
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
