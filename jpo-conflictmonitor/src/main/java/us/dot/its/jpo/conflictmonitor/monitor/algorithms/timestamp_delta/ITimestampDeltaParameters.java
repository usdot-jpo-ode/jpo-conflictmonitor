package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta;

public interface ITimestampDeltaParameters {


     String getAlgorithm();
     String getOutputTopicName();
     int getMaxDeltaMilliseconds();
     boolean isDebug();
     String getKeyStoreName();
     String getEventStoreName();
     int getRetentionTimeMinutes();
     String getNotificationTopicName();
}
