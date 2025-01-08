package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.spat;

public interface SpatMinimumDataAggregationAlgorithmFactory {

    SpatMinimumDataAggregationAlgorithm getAlgorithm(String algorithmName);

}
