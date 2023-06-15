package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event;

public interface BsmEventAlgorithmFactory {
    BsmEventAlgorithm getAlgorithm(String algorithmName);
}
