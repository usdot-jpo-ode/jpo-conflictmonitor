package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class SignalStateEventAggregator {
    private ArrayList<SignalStateEvent> events = new ArrayList<>();
    private long aggregatorCreationTime;
    private double tolerance;
    private long messageDurationDays;

    

    public SignalStateEventAggregator(){
        this.aggregatorCreationTime = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    @JsonIgnore
    public SignalStateEventAggregator add(SignalStateEvent event){
        events.add(event);

        for(SignalStateEvent previousEvents: this.events){
            if(previousEvents.getTimestamp() + (messageDurationDays * 3600*1000) < event.getTimestamp()){
                events.remove(previousEvents);
            }else{
                break;
            }
        }
        return this;
    }

    @JsonIgnore
    public SignalStateEventAssessment getSignalStateEventAssessment(){
        SignalStateEventAssessment assessment = new SignalStateEventAssessment();
        ArrayList<SignalStateEventAssessmentGroup> assessmentGroups = new ArrayList<>();
        HashMap<Integer,SignalStateEventAssessmentGroup> signalGroupLookup = new HashMap<>(); // laneId, Segment Index

        for(SignalStateEvent event : this.events){
            SignalStateEventAssessmentGroup signalGroup = signalGroupLookup.get(event.getSignalGroup());
            if(signalGroup == null){
                signalGroup = new SignalStateEventAssessmentGroup();
                assessmentGroups.add(signalGroup);
                signalGroupLookup.put(event.getSignalGroup(),signalGroup);
            }
            signalGroup.addSignalStateEvent(event);
            
        }
        
        assessment.setSignalStateAssessmentGroup(assessmentGroups);
        assessment.setTimestamp(ZonedDateTime.now().toInstant().toEpochMilli());

        return assessment;
    }

    public ArrayList<SignalStateEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<SignalStateEvent> events) {
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
