package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

public class LaneDirectionOfTravelAssessmentGroup {
    private int laneID;
    private int segmentID;
    private int inToleranceEvents;
    private int outOfToleranceEvents;
    private double medianInToleranceHeading;
    private double medianInToleranceCenterlineDistance;
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
}
