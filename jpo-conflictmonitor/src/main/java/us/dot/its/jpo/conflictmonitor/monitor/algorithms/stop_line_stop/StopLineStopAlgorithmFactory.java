package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop;

public interface StopLineStopAlgorithmFactory {
    StopLineStopAlgorithm getAlgorithm(String algorithmName);
}