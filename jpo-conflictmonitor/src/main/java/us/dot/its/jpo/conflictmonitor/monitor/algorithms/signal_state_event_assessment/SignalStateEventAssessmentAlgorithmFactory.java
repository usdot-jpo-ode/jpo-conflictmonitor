package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment;

public interface SignalStateEventAssessmentAlgorithmFactory {
    SignalStateEventAssessmentAlgorithm getAlgorithm(String algorithmName);
}
