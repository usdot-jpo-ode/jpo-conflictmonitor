package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.EventAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopLineStopAggregator {
    
    
    private ArrayList<StopLineStopEvent> events = new ArrayList<>();
    // private long messageDurationDays;
    private long aggregatorCreationTime;

    

    public StopLineStopAggregator(){
        
        this.aggregatorCreationTime = ZonedDateTime.now().toInstant().toEpochMilli();
    }


    @JsonIgnore
    public StopLineStopAggregator add(StopLineStopEvent event){
        
        // Skip stop line stop events where no connection was made.
        if(event.getSignalGroup() == -1){
            return this;
        }
        events.add(event);
        return this;
    }

    @JsonIgnore
    public StopLineStopAssessment getStopLineStopAssessment(long lookBackPeriodDays){

        long lastEventTime = ZonedDateTime.now().toInstant().toEpochMilli();
        if(this.events.size() > 0){
            lastEventTime = this.events.get(this.events.size() -1).getFinalTimestamp();
        }

        List<StopLineStopEvent> removeEvents = new ArrayList<>();
        for(StopLineStopEvent previousEvents: this.events){
            if(previousEvents.getFinalTimestamp() + (lookBackPeriodDays *24* 3600*1000) <  lastEventTime){
                removeEvents.add(previousEvents);
            }else{
                break;
            }
        }
        events.removeAll(removeEvents);

        StopLineStopAssessment assessment = new StopLineStopAssessment();
        ArrayList<StopLineStopAssessmentGroup> assessmentGroups = new ArrayList<>();
        HashMap<Integer,StopLineStopAssessmentGroup> signalGroupLookup = new HashMap<>(); // laneId, Segment Index

        int intersectionID = -1;
        int roadRegulatorID = -1;

        for(StopLineStopEvent event : this.events){
            intersectionID = event.getIntersectionID();
            roadRegulatorID = event.getRoadRegulatorID();
            StopLineStopAssessmentGroup signalGroup = signalGroupLookup.get(event.getSignalGroup());
            if(signalGroup == null){
                signalGroup = new StopLineStopAssessmentGroup();
                signalGroup.setSignalGroup(event.getSignalGroup());
                assessmentGroups.add(signalGroup);
                signalGroupLookup.put(event.getSignalGroup(),signalGroup);
            }
            signalGroup.addStopLineStopEvent(event);
            
        }
        
        assessment.setIntersectionID(intersectionID);
        assessment.setRoadRegulatorID(roadRegulatorID);
        assessment.setStopLineStopAssessmentGroup(assessmentGroups);
        assessment.setTimestamp(ZonedDateTime.now().toInstant().toEpochMilli());

        return assessment;
    }

    @JsonIgnore
    public EventAssessment getEventAssessmentPair(long lookBackPeriodDays){
        EventAssessment eventAssessment = new EventAssessment();
        eventAssessment.setAssessment(getStopLineStopAssessment(lookBackPeriodDays));
        if(this.events.size() >0){
            eventAssessment.setEvent(this.events.get(this.events.size()-1));
        }
        return eventAssessment;
    }
}
