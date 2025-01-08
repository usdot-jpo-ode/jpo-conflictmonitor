package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression;

public interface EventStateProgressionAggregationAlgorithmFactory {
    EventStateProgressionAggregationAlgorithm getAlgorithm(String algorithmName);
}
