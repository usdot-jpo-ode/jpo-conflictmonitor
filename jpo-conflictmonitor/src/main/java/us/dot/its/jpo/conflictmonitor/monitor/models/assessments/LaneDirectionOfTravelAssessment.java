package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.List;

public class LaneDirectionOfTravelAssessment extends Assessment{
    private int timestamp;
    private List<LaneDirectionOfTravelAssessmentGroup> laneDirectionOfTravelAssessmentGroup;
    
    public int getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<LaneDirectionOfTravelAssessmentGroup> getLaneDirectionOfTravelAssessmentGroup() {
        return laneDirectionOfTravelAssessmentGroup;
    }
    public void setLaneDirectionOfTravelAssessmentGroup(
            List<LaneDirectionOfTravelAssessmentGroup> laneDirectionOfTravelAssessmentGroup) {
        this.laneDirectionOfTravelAssessmentGroup = laneDirectionOfTravelAssessmentGroup;
    }

}
