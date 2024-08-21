package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat;

public interface SpatTimestampDeltaAlgorithmFactory {
    SpatTimestampDeltaAlgorithm getAlgorithm(String algorithmName);
}
