package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class ConnectionOfTravelAssessment extends Assessment{
    private long timestamp;
    private List<ConnectionOfTravelAssessmentGroup> connectionOfTravelAssessmentGroups = new ArrayList<>();

    public ConnectionOfTravelAssessment(){
        super("ConnectionOfTravel");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<ConnectionOfTravelAssessmentGroup> getConnectionOfTravelAssessment() {
        return connectionOfTravelAssessmentGroups;
    }

    public void setConnectionOfTravelAssessmentGroups(List<ConnectionOfTravelAssessmentGroup> connectionOfTravelAssessmentGroups) {
        this.connectionOfTravelAssessmentGroups = connectionOfTravelAssessmentGroups;
    }

    public ConnectionOfTravelAssessment add(ConnectionOfTravelEvent event){
        return this;
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
