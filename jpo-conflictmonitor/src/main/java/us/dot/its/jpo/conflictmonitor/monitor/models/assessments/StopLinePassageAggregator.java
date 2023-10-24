package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class StopLinePassageAggregator {
    private ArrayList<StopLinePassageEvent> events = new ArrayList<>();
    private long aggregatorCreationTime;
    private double tolerance;
    private long messageDurationDays;

    

    public StopLinePassageAggregator(){
        
        this.aggregatorCreationTime = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    @JsonIgnore
    public StopLinePassageAggregator add(StopLinePassageEvent event){
        
        // Skip stop line stop events where no connection was made.
        if(event.getSignalGroup() == -1){
            return this;
        }
        events.add(event);

        List<StopLinePassageEvent> removeEvents = new ArrayList<>();

        System.out.println("Adding New Event to Aggregation");

        for(StopLinePassageEvent previousEvents: this.events){
            if(previousEvents.getTimestamp() + (messageDurationDays*24 * 3600*1000) < event.getTimestamp()){
                System.out.println("Adding Event to Remove" + previousEvents.getTimestamp() + " " + event.getTimestamp());

                removeEvents.add(previousEvents);
            }else{
                break;
            }
        }
        events.removeAll(removeEvents);
        return this;
    }

    @JsonIgnore
    public StopLinePassageAssessment getSignalStateEventAssessment(){
        StopLinePassageAssessment assessment = new StopLinePassageAssessment();
        ArrayList<StopLinePassageAssessmentGroup> assessmentGroups = new ArrayList<>();
        HashMap<Integer,StopLinePassageAssessmentGroup> signalGroupLookup = new HashMap<>(); // laneId, Segment Index

        int intersectionID = -1;
        int roadRegulatorID = -1;

        System.out.println("Generating Stop Line Passage Event assesment" + this.events.size());

        for(StopLinePassageEvent event : this.events){
            intersectionID = event.getIntersectionID();
            roadRegulatorID = event.getRoadRegulatorID();
            StopLinePassageAssessmentGroup signalGroup = signalGroupLookup.get(event.getSignalGroup());
            if(signalGroup == null){
                signalGroup = new StopLinePassageAssessmentGroup();
                assessmentGroups.add(signalGroup);
                signalGroupLookup.put(event.getSignalGroup(),signalGroup);
            }
            signalGroup.addSignalStateEvent(event);
            
        }
        
        assessment.setIntersectionID(intersectionID);
        assessment.setRoadRegulatorID(roadRegulatorID);
        assessment.setSignalStateEventAssessmentGroup(assessmentGroups);
        assessment.setTimestamp(ZonedDateTime.now().toInstant().toEpochMilli());

        return assessment;
    }

    public ArrayList<StopLinePassageEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<StopLinePassageEvent> events) {
        this.events = events;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public long getAggregatorCreationTime() {
        return aggregatorCreationTime;
    }

    public void setAggregatorCreationTime(long aggregatorCreationTime) {
        this.aggregatorCreationTime = aggregatorCreationTime;
    }

    public long getMessageDurationDays() {
        return messageDurationDays;
    }

    public void setMessageDurationDays(long messageDurationDays) {
        this.messageDurationDays = messageDurationDays;
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
