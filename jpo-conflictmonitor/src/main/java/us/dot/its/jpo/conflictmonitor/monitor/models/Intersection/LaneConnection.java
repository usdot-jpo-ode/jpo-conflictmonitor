package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

public class LaneConnection {
    
    private Lane ingressLane;
    private Lane egressLane;
    private int connectionId;
    private int signalGroup;

    public LaneConnection(Lane ingressLane, Lane egressLane, int connectionId, int signalGroup){
        this.ingressLane = ingressLane;
        this.egressLane = egressLane;
        this.connectionId = connectionId;
        this.signalGroup = signalGroup;
    }

    public Lane getIngressLane() {
        return ingressLane;
    }

    public void setIngressLane(Lane ingressLane) {
        this.ingressLane = ingressLane;
    }
    
    public Lane getEgressLane() {
        return egressLane;
    }

    public void setEgressLane(Lane egressLane) {
        this.egressLane = egressLane;
    }
    
    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public int getSignalGroup() {
        return signalGroup;
    }

    public void setSignalGroup(int signalGroup) {
        this.signalGroup = signalGroup;
    }

}
