package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate;

public interface MapBroadcastRateParametersFactory {
    
    BroadcastRateParameters getParameters(String algorithmName);

}
