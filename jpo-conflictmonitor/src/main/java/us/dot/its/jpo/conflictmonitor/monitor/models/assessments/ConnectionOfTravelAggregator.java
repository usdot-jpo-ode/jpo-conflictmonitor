package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;

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

        // for(SignalStateEvent event : this.events){
        //     SignalStateEventAssessmentGroup signalGroup = signalGroupLookup.get(event.getSignalGroup());
        //     if(signalGroup == null){
        //         signalGroup = new SignalStateEventAssessmentGroup();
        //         assessmentGroups.add(signalGroup);
        //         signalGroupLookup.put(event.getSignalGroup(),signalGroup);
        //     }
        //     signalGroup.addSignalStateEvent(event);
            
        // }
        
        // assessment.setSignalStateAssessmentGroup(assessmentGroups);
        // assessment.setTimestamp(ZonedDateTime.now().toInstant().toEpochMilli());

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

    public String toString(){
        return "Connection of Travel Event Aggregator. Created At: " + this.aggregatorCreationTime + " Message Count: " + this.events.size() + "Intersection ID: " + this;
    }

    public long getMessageDurationDays() {
        return messageDurationDays;
    }

    public void setMessageDurationDays(long messageDurationDays) {
        this.messageDurationDays = messageDurationDays;
    }
    
}
