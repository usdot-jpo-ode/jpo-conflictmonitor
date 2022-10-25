package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.List;

public class ConnectionOfTravelAssessment {
    private int timestamp;
    private List<ConnectionOfTravelAssessmentGroup> ConnectionOfTravelAssessment;

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
}
