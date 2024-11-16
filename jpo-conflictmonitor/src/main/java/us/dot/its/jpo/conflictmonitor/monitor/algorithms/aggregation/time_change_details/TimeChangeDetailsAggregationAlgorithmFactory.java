package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details;

public interface TimeChangeDetailsAggregationAlgorithmFactory {
    TimeChangeDetailsAggregationAlgorithm getAlgorithm(String algorithmFactory);
}
