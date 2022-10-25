package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

public class LaneDirectionOfTravelAssessmentGroup {
    private int laneID;
    private int inToleranceEvents;
    private double medianInToleranceHeading;
    private int outOfToleranceEvents;

    public int getLaneID() {
        return laneID;
    }

    public void setLaneID(int laneID) {
        this.laneID = laneID;
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
}
