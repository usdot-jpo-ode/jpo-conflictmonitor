package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition;

public interface SpatTransitionAlgorithmFactory {
    SpatTransitionAlgorithm getAlgorithm(String algorithmName);
}
