package us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification;

public interface NotificationAlgorithmFactory {
    NotificationAlgorithm getAlgorithm(String algorithmName);
}
