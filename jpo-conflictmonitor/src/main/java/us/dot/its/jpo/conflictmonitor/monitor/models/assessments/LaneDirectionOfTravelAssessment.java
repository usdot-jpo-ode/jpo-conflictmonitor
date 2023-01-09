package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;

public class LaneDirectionOfTravelAssessment extends Assessment{
    private long timestamp;
    private int roadRegulatorID;
    private int intersectionID;
    private List<LaneDirectionOfTravelAssessmentGroup> laneDirectionOfTravelAssessmentGroup = new ArrayList<>();
    
    
    

    public LaneDirectionOfTravelAssessment add(LaneDirectionOfTravelEvent event){
        return this;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRoadRegulatorID() {
        return roadRegulatorID;
    }

    public void setRoadRegulatorID(int roadRegulatorID) {
        this.roadRegulatorID = roadRegulatorID;
    } 
       
    public int getIntersectionID() {
        return intersectionID;
    }

    public void setIntersectionID(int intersectionID) {
        this.intersectionID = intersectionID;
    }

    public List<LaneDirectionOfTravelAssessmentGroup> getLaneDirectionOfTravelAssessmentGroup() {
        return laneDirectionOfTravelAssessmentGroup;
    }

    public void setLaneDirectionOfTravelAssessmentGroup(
            List<LaneDirectionOfTravelAssessmentGroup> laneDirectionOfTravelAssessmentGroup) {
        this.laneDirectionOfTravelAssessmentGroup = laneDirectionOfTravelAssessmentGroup;
    }

    

}
