package us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition;

public interface RepartitionAlgorithmFactory {
    RepartitionAlgorithm getAlgorithm(String algorithmName);
}
