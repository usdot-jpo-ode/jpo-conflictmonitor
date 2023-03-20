package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;


import org.springframework.data.mongodb.core.mapping.Document;

@Document("CmLaneDirectionOfTravelAssessment")
public class LaneDirectionOfTravelAssessment extends Assessment{
    private long timestamp;
    private List<LaneDirectionOfTravelAssessmentGroup> laneDirectionOfTravelAssessmentGroup = new ArrayList<>();
    
    public LaneDirectionOfTravelAssessment(){
        super("LaneDirectionOfTravel");
    }
    

    public LaneDirectionOfTravelAssessment add(LaneDirectionOfTravelEvent event){
        return this;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<LaneDirectionOfTravelAssessmentGroup> getLaneDirectionOfTravelAssessmentGroup() {
        return laneDirectionOfTravelAssessmentGroup;
    }

    public void setLaneDirectionOfTravelAssessmentGroup(
            List<LaneDirectionOfTravelAssessmentGroup> laneDirectionOfTravelAssessmentGroup) {
        this.laneDirectionOfTravelAssessmentGroup = laneDirectionOfTravelAssessmentGroup;
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
