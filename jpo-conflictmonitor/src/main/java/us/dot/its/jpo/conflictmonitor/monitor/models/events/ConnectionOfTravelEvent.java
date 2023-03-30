package us.dot.its.jpo.conflictmonitor.monitor.models.events;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document("CmConnectionOfTravelEvent")
public class ConnectionOfTravelEvent extends Event{
    private long timestamp;
    private int ingressLaneID;
    private int egressLaneID;
    private int connectionID; // unknown value allowed

    public ConnectionOfTravelEvent(){
        super("ConnectionOfTravel");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIngressLaneID() {
        return ingressLaneID;
    }

    public void setIngressLaneID(int ingressLaneId) {
        this.ingressLaneID = ingressLaneId;
    }

    public int getEgressLaneID() {
        return egressLaneID;
    }

    public void setEgressLaneID(int egressLaneId) {
        this.egressLaneID = egressLaneId;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionId) {
        this.connectionID = connectionId;
    }

    @JsonIgnore
    public String getKey(){
        return this.getIntersectionID() + "_" + this.getIngressLaneID() + "_" + this.getEgressLaneID();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ConnectionOfTravelEvent)) {
            return false;
        }
        
        ConnectionOfTravelEvent connectionOfTravelEvent = (ConnectionOfTravelEvent) o;
        return 
            this.getTimestamp() == connectionOfTravelEvent.getTimestamp() &&
            this.getRoadRegulatorID() == connectionOfTravelEvent.getRoadRegulatorID() &&
            this.getIntersectionID() == connectionOfTravelEvent.getIntersectionID() &&
            this.getIngressLaneID() == connectionOfTravelEvent.getIngressLaneID() &&
            this.getEgressLaneID() == connectionOfTravelEvent.getEgressLaneID() &&
            this.getConnectionID() == connectionOfTravelEvent.getConnectionID();
            
    }

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return testReturn;
    }
}
