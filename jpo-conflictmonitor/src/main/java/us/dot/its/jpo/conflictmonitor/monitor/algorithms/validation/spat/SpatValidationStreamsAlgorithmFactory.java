package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat;

public interface SpatValidationStreamsAlgorithmFactory { 
    SpatValidationAlgorithm getAlgorithm(String algorithmName);
}
