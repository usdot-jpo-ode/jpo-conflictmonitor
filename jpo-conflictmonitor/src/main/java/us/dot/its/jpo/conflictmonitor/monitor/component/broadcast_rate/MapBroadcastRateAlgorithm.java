package us.dot.its.jpo.conflictmonitor.monitor.component.broadcast_rate;


public abstract class BaseMapBroadcastRateAlgorithm
        implements BroadcastRateAlgorithm {


    private BroadcastRateParameters parameters;

    @Override
    public void setParameters(BroadcastRateParameters parameters) {
        this.parameters = parameters;
    }
    
    
}
