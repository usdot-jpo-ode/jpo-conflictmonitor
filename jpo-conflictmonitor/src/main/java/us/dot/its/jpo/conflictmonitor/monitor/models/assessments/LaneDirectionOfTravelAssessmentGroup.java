package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;

public class LaneDirectionOfTravelAssessmentGroup {
    private int laneID;
    private int segmentID;
    private int inToleranceEvents;
    private double medianInToleranceHeading;
    private int outOfToleranceEvents;
    private ArrayList<Double> headings;
    private ArrayList<Double> offsets;
    private double tolerance;

    
    // @JsonIgnore
    // public LaneDirectionOfTravelAssessmentGroup(int laneID, int segmentID, double tolerance){
    //     this.laneID = laneID;
    //     this.segmentID = segmentID;
    //     this.tolerance = tolerance;
    // }

    public void add(LaneDirectionOfTravelEvent event){
        headings.add(event.getMedianVehicleHeading());
        offsets.add(event.getMedianDistanceFromCenterline());
    }

    public int getLaneID() {
        return laneID;
    }

    public void setLaneID(int laneID) {
        this.laneID = laneID;
    }

    public int getSegmentID() {
        return segmentID;
    }

    public void setSegmentID(int segmentID) {
        this.segmentID = segmentID;
    }

    public int getInToleranceEvents() {
        return inToleranceEvents;
    }

    public void setInToleranceEvents(int inToleranceEvents) {
        this.inToleranceEvents = inToleranceEvents;
    }

    public double getMedianInToleranceHeading() {
        return medianInToleranceHeading;
    }

    public void setMedianInToleranceHeading(double medianInToleranceHeading) {
        this.medianInToleranceHeading = medianInToleranceHeading;
    }

    public int getOutOfToleranceEvents() {
        return outOfToleranceEvents;
    }

    public void setOutOfToleranceEvents(int outOfToleranceEvents) {
        this.outOfToleranceEvents = outOfToleranceEvents;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }
}
