package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression;

public interface SpatMessageCountProgressionAlgorithmFactory {
    SpatMessageCountProgressionAlgorithm getAlgorithm(String algorithmName);
}
