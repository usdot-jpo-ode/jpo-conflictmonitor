package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_revision_counter;

public interface SpatMessageCountProgressionAlgorithmFactory {
    SpatMessageCountProgressionAlgorithm getAlgorithm(String algorithmName);
}
