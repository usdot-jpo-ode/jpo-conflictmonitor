package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

public class LaneConnection {
    
    private Lane ingressLane;
    private Lane egressLane;
    private int connectionId;

    public LaneConnection(Lane ingressLane, Lane egressLane, int connectionId){
        this.ingressLane = ingressLane;
        this.egressLane = egressLane;
        this.connectionId = connectionId;
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

}
