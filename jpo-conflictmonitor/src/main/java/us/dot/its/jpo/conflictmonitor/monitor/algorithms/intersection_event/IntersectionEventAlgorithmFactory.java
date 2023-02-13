package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event;

public interface IntersectionEventAlgorithmFactory {
    IntersectionEventAlgorithm getAlgorithm(String algorithmName);
}
