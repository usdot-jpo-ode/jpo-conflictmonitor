package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

public class LaneDirectionOfTravelAssessmentGroup {
    private int laneID;
    private int segmentID;
    
    private int inToleranceEvents;
    private int outOfToleranceEvents;
    
    private double medianInToleranceHeading;
    private double medianInToleranceCenterlineDistance;
    private double medianHeading;
    private double medianCenterlineDistance;

    private double expectedHeading; 

    

    private double tolerance;



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

    public double getMedianInToleranceCenterlineDistance() {
        return medianInToleranceCenterlineDistance;
    }

    public void setMedianInToleranceCenterlineDistance(double medianInToleranceCenterlineDistance) {
        this.medianInToleranceCenterlineDistance = medianInToleranceCenterlineDistance;
    }

    public double getMedianHeading() {
        return medianHeading;
    }

    public void setMedianHeading(double medianHeading) {
        this.medianHeading = medianHeading;
    }

    public double getMedianCenterlineDistance() {
        return medianCenterlineDistance;
    }

    public void setMedianCenterlineDistance(double medianCenterlineDistance) {
        this.medianCenterlineDistance = medianCenterlineDistance;
    }

    public double getExpectedHeading() {
        return expectedHeading;
    }

    public void setExpectedHeading(double expectedHeading) {
        this.expectedHeading = expectedHeading;
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
