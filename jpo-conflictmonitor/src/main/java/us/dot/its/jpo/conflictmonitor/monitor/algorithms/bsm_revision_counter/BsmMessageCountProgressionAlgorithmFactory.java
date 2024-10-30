package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_revision_counter;

public interface BsmMessageCountProgressionAlgorithmFactory {
    BsmMessageCountProgressionAlgorithm getAlgorithm(String algorithmName);
}
