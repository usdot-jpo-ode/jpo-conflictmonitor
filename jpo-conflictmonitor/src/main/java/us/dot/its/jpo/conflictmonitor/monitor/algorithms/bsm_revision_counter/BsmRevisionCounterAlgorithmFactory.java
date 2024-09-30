package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_revision_counter;

public interface BsmRevisionCounterAlgorithmFactory {
    BsmRevisionCounterAlgorithm getAlgorithm(String algorithmName);
}
