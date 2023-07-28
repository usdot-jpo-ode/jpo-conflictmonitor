package us.dot.its.jpo.conflictmonitor.monitor.algorithms.event;

public interface EventAlgorithmFactory {
    EventAlgorithm getAlgorithm(String algorithmName);
}
