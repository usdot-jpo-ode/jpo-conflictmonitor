package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression;

public interface BsmMessageCountProgressionAlgorithmFactory {
    BsmMessageCountProgressionAlgorithm getAlgorithm(String algorithmName);
}
