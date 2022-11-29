package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat;

public interface SpatBroadcastRateAlgorithmFactory { 
    SpatBroadcastRateAlgorithm getAlgorithm(String algorithmName);
}
