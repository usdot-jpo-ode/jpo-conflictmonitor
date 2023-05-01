package us.dot.its.jpo.conflictmonitor.monitor.models.events;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document("CmConnectionOfTravelEvent")
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@Generated
public class ConnectionOfTravelEvent extends Event{
    private long timestamp;
    private int ingressLaneID;
    private int egressLaneID;
    private int connectionID; // unknown value allowed

    public ConnectionOfTravelEvent(){
        super("ConnectionOfTravel");
    }

    @JsonIgnore
    public String getKey(){
        return this.getIntersectionID() + "_" + this.getIngressLaneID() + "_" + this.getEgressLaneID();
    }
}
