package us.dot.its.jpo.conflictmonitor.monitor.models.events;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ConnectionOfTravelEvent extends Event{
    private long timestamp;
    private int roadRegulatorId;
    private int intersectionId;
    private int ingressLaneId;
    private int egressLaneId;
    private int connectionId; // unknown value allowed

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRoadRegulatorId() {
        return roadRegulatorId;
    }

    public void setRoadRegulatorId(int roadRegulatorId) {
        this.roadRegulatorId = roadRegulatorId;
    }

    public int getIntersectionId() {
        return intersectionId;
    }

    public void setIntersectionId(int intersectionId) {
        this.intersectionId = intersectionId;
    }

    public int getIngressLaneId() {
        return ingressLaneId;
    }

    public void setIngressLaneId(int ingressLaneId) {
        this.ingressLaneId = ingressLaneId;
    }

    public int getEgressLaneId() {
        return egressLaneId;
    }

    public void setEgressLaneId(int egressLaneId) {
        this.egressLaneId = egressLaneId;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    @JsonIgnore
    public String getKey(){
        return this.intersectionId + "_" + this.ingressLaneId + "_" + this.egressLaneId;
    }

    @Override
    public String toString(){
        return "Connection of Travel Event: Intersection: " + this.intersectionId + " Ingress: " + this.ingressLaneId + "Egress: " + this.egressLaneId;
    }
}
