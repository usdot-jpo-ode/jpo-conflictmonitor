package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage;

public interface StopLinePassageAlgorithmFactory {
    StopLinePassageAlgorithm getAlgorithm(String algorithmName);
}
