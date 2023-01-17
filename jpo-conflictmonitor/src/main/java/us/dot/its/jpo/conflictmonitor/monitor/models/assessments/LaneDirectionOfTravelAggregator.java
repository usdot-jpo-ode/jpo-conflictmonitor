package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.utils.MathFunctions;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class LaneDirectionOfTravelAggregator {
    private ArrayList<LaneDirectionOfTravelEvent> events = new ArrayList<>();
    private long aggregatorCreationTime;
    private double tolerance;
    private long messageDurationDays;

    

    public LaneDirectionOfTravelAggregator(){
        this.aggregatorCreationTime = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    @JsonIgnore
    public LaneDirectionOfTravelAggregator add(LaneDirectionOfTravelEvent event){
        events.add(event);

        for(LaneDirectionOfTravelEvent previousEvents: this.events){
            if(previousEvents.getTimestamp() + (messageDurationDays * 3600*1000) < event.getTimestamp()){
                events.remove(previousEvents);
            }else{
                break;
            }
        }
        return this;
    }

    @JsonIgnore
    public LaneDirectionOfTravelAssessment getLaneDirectionOfTravelAssessment(){
        LaneDirectionOfTravelAssessment assessment = new LaneDirectionOfTravelAssessment();
        ArrayList<LaneDirectionOfTravelAssessmentGroup> assessmentGroups = new ArrayList<>();
        HashMap<Integer,HashMap<Integer,ArrayList<LaneDirectionOfTravelEvent>>> laneGroupLookup = new HashMap<>(); // laneId, Segment Index

        for(LaneDirectionOfTravelEvent event : this.events){
            assessment.setIntersectionID(event.getIntersectionID());
            assessment.setRoadRegulatorID(event.getRoadRegulatorID());
            
            HashMap<Integer, ArrayList<LaneDirectionOfTravelEvent>> laneLookup = laneGroupLookup.get(event.getLaneID());
            if(laneLookup == null){
                laneLookup = new HashMap<Integer, ArrayList<LaneDirectionOfTravelEvent>>();
                laneGroupLookup.put(event.getLaneID(), laneLookup);
            }
            
            

            ArrayList<LaneDirectionOfTravelEvent> groupSegment = laneLookup.get(event.getLaneSegmentNumber());

            if(groupSegment == null){
                groupSegment = new ArrayList<LaneDirectionOfTravelEvent>();
                laneLookup.put(event.getLaneSegmentNumber(), groupSegment);
            }

            groupSegment.add(event);
        }

        for(Entry<Integer, HashMap<Integer, ArrayList<LaneDirectionOfTravelEvent>>> entry: laneGroupLookup.entrySet()){
            for(Entry<Integer, ArrayList<LaneDirectionOfTravelEvent>> groups: entry.getValue().entrySet()){
                LaneDirectionOfTravelAssessmentGroup group = new LaneDirectionOfTravelAssessmentGroup();
                ArrayList<Double> headings = new ArrayList<>();
                ArrayList<Double> distances = new ArrayList<>();

                group.setLaneID(entry.getKey());
                group.setSegmentID(groups.getKey());
                
                
                int inTolerance = 0;
                int outOfTolerance = 0;


                for(LaneDirectionOfTravelEvent event: groups.getValue()){
                    if(Math.abs(event.getMedianVehicleHeading() - event.getExpectedHeading()) > tolerance){
                        outOfTolerance +=1;
                    }else{
                        inTolerance +=1;
                        headings.add(event.getMedianVehicleHeading());
                        distances.add(event.getMedianDistanceFromCenterline());
                    }
                }

                group.setInToleranceEvents(inTolerance);
                group.setOutOfToleranceEvents(outOfTolerance);
                group.setMedianInToleranceHeading(MathFunctions.getMedian(headings));
                group.setMedianInToleranceCenterlineDistance(MathFunctions.getMedian(distances));
                group.setTolerance(tolerance);
                assessmentGroups.add(group);
            }
        }
        
        assessment.setLaneDirectionOfTravelAssessmentGroup(assessmentGroups);
        assessment.setTimestamp(ZonedDateTime.now().toInstant().toEpochMilli());

        return assessment;
    }

    public ArrayList<LaneDirectionOfTravelEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<LaneDirectionOfTravelEvent> events) {
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
