package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta;

import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

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
