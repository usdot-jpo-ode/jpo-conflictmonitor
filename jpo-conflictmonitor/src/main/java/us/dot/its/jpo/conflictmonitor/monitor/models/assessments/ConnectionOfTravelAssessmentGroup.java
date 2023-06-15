package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

@Getter
@Setter
@Generated
public class ConnectionOfTravelAssessmentGroup {
    private int ingressLaneID;
    private int egressLaneID;
    private int connectionID; // may be empty
    private int eventCount;

    
    @JsonIgnore
    public void addConnectionOfTravelEvent(ConnectionOfTravelEvent event){
        this.eventCount +=1;
    }



}
