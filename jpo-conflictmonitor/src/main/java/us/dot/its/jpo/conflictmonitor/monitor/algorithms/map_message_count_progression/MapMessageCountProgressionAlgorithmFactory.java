package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression;

public interface MapMessageCountProgressionAlgorithmFactory {
    MapMessageCountProgressionAlgorithm getAlgorithm(String algorithmName);
}
