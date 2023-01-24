package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_reference_alignment_notification;


public interface IntersectionReferenceAlignmentNotificationAlgorithmFactory {
    IntersectionReferenceAlignmentNotificationAlgorithm getAlgorithm(String algorithmName);
}
