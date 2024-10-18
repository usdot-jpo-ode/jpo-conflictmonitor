package us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression;

public interface EventStateProgressionAlgorithmFactory {
    EventStateProgressionAlgorithm getAlgorithm(String algorithmName);
}
