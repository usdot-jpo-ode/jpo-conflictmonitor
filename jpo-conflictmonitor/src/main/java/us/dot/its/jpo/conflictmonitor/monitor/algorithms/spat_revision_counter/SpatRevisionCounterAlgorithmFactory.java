package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_revision_counter;

public interface SpatRevisionCounterAlgorithmFactory {
    SpatRevisionCounterAlgorithm getAlgorithm(String algorithmName);
}
