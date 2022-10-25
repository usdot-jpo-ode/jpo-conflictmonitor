package us.dot.its.jpo.conflictmonitor.monitor.models.assessments;

public class ConnectionOfTravelAssessmentGroup {
    private int ingressLaneID;
    private int egressLaneID;
    private int connectionID; // may be empty
    private int eventCount;

    public int getIngressLaneID() {
        return ingressLaneID;
    }

    public void setIngressLaneID(int ingressLaneID) {
        this.ingressLaneID = ingressLaneID;
    }

    public int getEgressLaneID() {
        return egressLaneID;
    }

    public void setEgressLaneID(int egressLaneID) {
        this.egressLaneID = egressLaneID;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }
}
