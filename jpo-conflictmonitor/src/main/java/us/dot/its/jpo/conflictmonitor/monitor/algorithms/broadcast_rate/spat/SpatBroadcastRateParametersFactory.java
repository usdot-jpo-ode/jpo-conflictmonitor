package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat;

public interface SpatBroadcastRateParametersFactory {
    SpatBroadcastRateParameters getParameters(String configurationName);
}
