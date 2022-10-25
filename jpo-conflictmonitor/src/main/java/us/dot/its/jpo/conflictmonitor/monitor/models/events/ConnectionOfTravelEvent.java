package us.dot.its.jpo.conflictmonitor.monitor.models.events;

public class ConnectionOfTravelEvent {
    private int timestamp;
    private int roadRegulatorID;
    private int intersectionID;
    private int ingressLaneID;
    private int egressLaneID;
    private int connectionID; // unknown value allowed

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getRoadRegulatorID() {
        return roadRegulatorID;
    }

    public void setRoadRegulatorID(int roadRegulatorID) {
        this.roadRegulatorID = roadRegulatorID;
    }

    public int getIntersectionID() {
        return intersectionID;
    }

    public void setIntersectionID(int intersectionID) {
        this.intersectionID = intersectionID;
    }

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
}
