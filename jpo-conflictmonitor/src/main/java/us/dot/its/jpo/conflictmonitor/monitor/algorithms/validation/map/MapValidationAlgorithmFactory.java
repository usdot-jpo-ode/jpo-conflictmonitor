package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map;

public interface MapValidationAlgorithmFactory  {
    MapValidationAlgorithm getAlgorithm(String algorithmName);
}
