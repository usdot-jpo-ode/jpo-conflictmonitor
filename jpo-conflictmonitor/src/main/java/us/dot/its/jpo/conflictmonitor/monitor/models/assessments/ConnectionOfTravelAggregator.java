package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class ConnectionOfTravelAggregator {
    private ArrayList<ConnectionOfTravelEvent> events = new ArrayList<>();
    private long aggregatorCreationTime;
    private long messageDurationDays;

    

    public ConnectionOfTravelAggregator(){
        this.aggregatorCreationTime = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    @JsonIgnore
    public ConnectionOfTravelAggregator add(ConnectionOfTravelEvent event){
        events.add(event);

        for(ConnectionOfTravelEvent previousEvents: this.events){
            if(previousEvents.getTimestamp() + (messageDurationDays * 3600*1000) < event.getTimestamp()){
                events.remove(previousEvents);
            }else{
                break;
            }
        }
        return this;
    }

    @JsonIgnore
    public ConnectionOfTravelAssessment getConnectionOfTravelAssessment(){
        ConnectionOfTravelAssessment assessment = new ConnectionOfTravelAssessment();
        ArrayList<ConnectionOfTravelAssessmentGroup> assessmentGroups = new ArrayList<>();
        HashMap<String,ConnectionOfTravelAssessmentGroup> connectionGroupLookup = new HashMap<>(); // laneId, Segment Index

        for(ConnectionOfTravelEvent event : this.events){
            String eventKey = getEventKey(event);
            ConnectionOfTravelAssessmentGroup connectionGroup = connectionGroupLookup.get(eventKey);
            if(connectionGroup == null){
                connectionGroup = new ConnectionOfTravelAssessmentGroup();
                connectionGroup.setIngressLaneID(event.getIngressLaneId());
                connectionGroup.setEgressLaneID(event.getEgressLaneId());
                connectionGroup.setConnectionID(event.getConnectionId());
                assessmentGroups.add(connectionGroup);
                connectionGroupLookup.put(eventKey,connectionGroup);
            }
            connectionGroup.addConnectionOfTravelEvent(event);
        }
        
        assessment.setConnectionOfTravelAssessmentGroups(assessmentGroups);
        assessment.setTimestamp(ZonedDateTime.now().toInstant().toEpochMilli());

        return assessment;
    }

    public String getEventKey(ConnectionOfTravelEvent event){
        return event.getIngressLaneId() + "-" + event.getEgressLaneId();
    }

    public ArrayList<ConnectionOfTravelEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<ConnectionOfTravelEvent> events) {
        this.events = events;
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
