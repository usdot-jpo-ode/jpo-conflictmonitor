package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.rtcm;

public interface RtcmValidationAlgorithmFactory {
    RtcmValidationAlgorithm getAlgorithm(String algorithmName);
}
