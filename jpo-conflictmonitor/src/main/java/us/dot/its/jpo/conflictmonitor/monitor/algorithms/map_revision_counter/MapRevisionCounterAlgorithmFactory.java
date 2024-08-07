package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter;

public interface MapRevisionCounterAlgorithmFactory {
    MapRevisionCounterAlgorithm getAlgorithm(String algorithmName);
}
