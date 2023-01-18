package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class ConnectionOfTravelAssessmentGroup {
    private int ingressLaneID;
    private int egressLaneID;
    private int connectionID; // may be empty
    private int eventCount;

    
    @JsonIgnore
    public void addConnectionOfTravelEvent(ConnectionOfTravelEvent event){
        this.eventCount +=1;
    }

    public int getIngressLaneID() {
        return ingressLaneID;
    }

    public void setIngressLaneID(int ingressLaneID) {
        this.ingressLaneID = ingressLaneID;
    }

    public int getEgressLaneID() {
        return egressLaneID;
    }

    public void setEgressLaneID(int egressLaneID) {
        this.egressLaneID = egressLaneID;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
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
