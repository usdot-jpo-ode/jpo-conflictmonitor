package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

public interface SpatMinimumDataAggregationAlgorithmFactory {

    SpatMinimumDataAggregationAlgorithm getAlgorithm(String algorithmName);

}
