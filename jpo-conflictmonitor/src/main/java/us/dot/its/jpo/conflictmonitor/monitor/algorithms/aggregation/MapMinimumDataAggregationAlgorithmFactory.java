package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

public interface MapMinimumDataAggregationAlgorithmFactory {

    MapMinimumDataAggregationAlgorithm getAlgorithm(String algorithmName);

}
