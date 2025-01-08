package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

public interface SignalStateConflictAggregationAlgorithmFactory {
    SignalStateConflictAggregationAlgorithm getAlgorithm(String algorithmName);
}
