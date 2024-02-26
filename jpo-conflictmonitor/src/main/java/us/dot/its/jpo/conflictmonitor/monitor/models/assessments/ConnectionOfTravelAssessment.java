package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;

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
public class ConnectionOfTravelAssessment extends Assessment{
    private long timestamp;
    private List<ConnectionOfTravelAssessmentGroup> connectionOfTravelAssessmentGroups = new ArrayList<>();

    public ConnectionOfTravelAssessment(){
        super("ConnectionOfTravel");
    }



    public ConnectionOfTravelAssessment add(ConnectionOfTravelEvent event){
        return this;
    }


}
