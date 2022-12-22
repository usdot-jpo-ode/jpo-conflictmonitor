package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;

public class ConnectionOfTravelAssessment extends Assessment{
    private int timestamp;
    private List<ConnectionOfTravelAssessmentGroup> ConnectionOfTravelAssessment = new ArrayList<>();

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<ConnectionOfTravelAssessmentGroup> getConnectionOfTravelAssessment() {
        return ConnectionOfTravelAssessment;
    }

    public void setConnectionOfTravelAssessment(List<ConnectionOfTravelAssessmentGroup> connectionOfTravelAssessment) {
        ConnectionOfTravelAssessment = connectionOfTravelAssessment;
    }

    public ConnectionOfTravelAssessment add(ConnectionOfTravelEvent event){
        return this;
    }
}
