package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map;

public interface MapTimestampDeltaAlgorithmFactory {
    MapTimestampDeltaAlgorithm getAlgorithm(String algorithmName);
}
