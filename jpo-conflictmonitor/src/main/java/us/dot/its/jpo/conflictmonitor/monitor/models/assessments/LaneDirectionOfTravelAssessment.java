package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;


import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmLaneDirectionOfTravelAssessment")
@Getter
@Setter
@Generated
public class LaneDirectionOfTravelAssessment extends Assessment{
    private long timestamp;
    private List<LaneDirectionOfTravelAssessmentGroup> laneDirectionOfTravelAssessmentGroup = new ArrayList<>();
    
    public LaneDirectionOfTravelAssessment(){
        super("LaneDirectionOfTravel");
    }


    public LaneDirectionOfTravelAssessment add(LaneDirectionOfTravelEvent event){
        return this;
    }
}
