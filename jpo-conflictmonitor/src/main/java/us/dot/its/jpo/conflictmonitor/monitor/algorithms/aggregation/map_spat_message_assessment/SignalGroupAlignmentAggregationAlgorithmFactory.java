package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment;

public interface SignalGroupAlignmentAggregationAlgorithmFactory {
    SignalGroupAlignmentAggregationAlgorithm getAlgorithm(String algorithmName);
}
