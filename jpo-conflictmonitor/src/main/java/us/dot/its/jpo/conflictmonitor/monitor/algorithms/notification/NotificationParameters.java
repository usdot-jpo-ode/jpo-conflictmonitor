package us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "notification")
@ConfigDataClass
public class NotificationParameters {
    
    // Whether to log diagnostic information for debugging
    @ConfigData(key = "notification.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "notification.notificationOutputTopicName", 
        description = "The name of the topic to output notifications to", 
        updateType = READ_ONLY)
    String notificationOutputTopicName;

    @ConfigData(key = "notification.connectionOfTravelNotificationTopicName", 
        description = "The name of the topic to read Connection of Travel Notifications from", 
        updateType = READ_ONLY)
    String connectionOfTravelNotificationTopicName;

    @ConfigData(key = "notification.laneDirectionOfTravelNotificationTopicName", 
        description = "The name of the topic to read Lane Direction of Travel Notifications from", 
        updateType = READ_ONLY)
    String laneDirectionOfTravelNotificationTopicName;

    @ConfigData(key = "notification.intersectionReferenceAlignmentNotificationTopicName", 
        description = "The name of the topic to read Intersection Reference Alignment Notifications from", 
        updateType = READ_ONLY)
    String intersectionReferenceAlignmentNotificationTopicName;

    @ConfigData(key = "notification.signalGroupAlignmentNotificationTopicName", 
        description = "The name of the topic to read Signal Group Alignment Notifications from", 
        updateType = READ_ONLY)
    String signalGroupAlignmentNotificationTopicName;

    @ConfigData(key = "notification.signalStateConflictNotificationTopicName", 
        description = "The name of the topic to read Signal State Conflict Notifications from", 
        updateType = READ_ONLY)
    String signalStateConflictNotificationTopicName;

    @ConfigData(key = "notification.spatTimeChangeDetailsNotificationTopicName", 
        description = "The name of the topic to read Spat Time Change Details from", 
        updateType = READ_ONLY)
    String spatTimeChangeDetailsNotificationTopicName;

    @ConfigData(key = "notification.timestampDeltaNotificationTopicName",
        description = "The name of the topic to read MAP and SPAT Timestamp Delta Notifications from",
        updateType = READ_ONLY)
    String timestampDeltaNotificationTopicName;

    @ConfigData(key = "notification.topic.CmSpatTransitionNotification", description = "The name of the topic to read SPaT Transition Notifications from", updateType = READ_ONLY)
    String spatTransitionNotificationTopicName;
}
