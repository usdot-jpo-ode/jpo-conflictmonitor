package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPairList;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigMap;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUtil.getIntersectionValue;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "map.spat.message.assessment")
@ConfigDataClass
public class MapSpatMessageAssessmentParameters {
    
    // Whether to log diagnostic information for debugging
    @ConfigData(key = "map.spat.message.assessment.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "map.spat.message.assessment.mapInputTopicName", 
        description = "The name of the topic to read MAP messages from", 
        updateType = READ_ONLY)
    String mapInputTopicName;

    @ConfigData(key = "map.spat.message.assessment.spatInputTopicName", 
        description = "The name of the topic to read SPAT messages from", 
        updateType = READ_ONLY)
    String spatInputTopicName;

    @ConfigData(key = "map.spat.message.assessment.signalGroupAlignmentEventTopicName", 
        description = "The name of the topic to write signal group alignment events to", 
        updateType = READ_ONLY)
    String signalGroupAlignmentEventTopicName;

    @ConfigData(key = "map.spat.message.assessment.intersectionReferenceAlignmentEventTopicName", 
        description = "The name of the topic to write intersection reference alignment events to", 
        updateType = READ_ONLY)
    String intersectionReferenceAlignmentEventTopicName;

    @ConfigData(key = "map.spat.message.assessment.signalStateConflictEventTopicName", 
        description = "The name of the topic to write signal state conflict events to", 
        updateType = READ_ONLY)
    String signalStateConflictEventTopicName;

    @ConfigData(key = "map.spat.message.assessment.intersectionReferenceAlignmentNotificationTopicName", 
        description = "The name of the topic to write intersection reference alignment notifications to", 
        updateType = READ_ONLY)
    String intersectionReferenceAlignmentNotificationTopicName;

    @ConfigData(key = "map.spat.message.assessment.signalGroupAlignmentNotificationTopicName", 
        description = "The name of the topic to write signal group alignment notifications to", 
        updateType = READ_ONLY)
    String signalGroupAlignmentNotificationTopicName;

    @ConfigData(key = "map.spat.message.assessment.signalStateConflictNotificationTopicName", 
        description = "The name of the topic to write signal state conflict notifications to", 
        updateType = READ_ONLY)
    String signalStateConflictNotificationTopicName;

    @ConfigData(key = "map.spat.message.assessment.aggregateIntersectionReferenceAlignmentEvents",
        description = "Whether to aggregate Intersection Reference Alignment Events",
        updateType = READ_ONLY)
    boolean aggregateIntersectionReferenceAlignmentEvents;

    @ConfigData(key = "map.spat.message.assessment.aggregateSignalGroupAlignmentEvents",
        description = "Whether to aggregate Signal Group Alignment Events",
        updateType = READ_ONLY)
    boolean aggregateSignalGroupAlignmentEvents;

    @ConfigData(key = "map.spat.message.assessment.aggregateSignalStateConflictEvents",
        description = "Whether to aggregate Signal State Conflict Events",
        updateType = READ_ONLY)
    boolean aggregateSignalStateConflictEvents;

    @ConfigData(key = "spat.time.change.details.intersectionReferenceAlignmentNotificationAggTopicName",
            description = "The name of the topic to write intersection reference alignment notifications to",
            updateType = READ_ONLY)
    String intersectionReferenceAlignmentNotificationAggTopicName;

    @ConfigData(key = "spat.time.change.details.signalGroupAlignmentNotificationAggTopicName",
            description = "The name of the topic to write signal group alignment notifications to",
            updateType = READ_ONLY)
    String signalGroupAlignmentNotificationAggTopicName;

    @ConfigData(key = "spat.time.change.details.signalStateConflictNotificationAggTopicName",
            description = "The name of the topic to write signal state conflict notifications to",
            updateType = READ_ONLY)
    String signalStateConflictNotificationAggTopicName;


    @ConfigData(key = "map.spat.message.assessment.concurrentPermissiveList",
        description = "List of pairs of lane connections that are allowed concurrently permissive for an intersection.",
        updateType = INTERSECTION)
    volatile ConnectedLanesPairList concurrentPermissiveList;

    //
    // Maps for parameters that can be customized at the intersection level
    //
    final ConfigMap<ConnectedLanesPairList> concurrentPermissiveListMap = new ConfigMap<>();

    //
    // Intersection-specific properties
    //
    public ConnectedLanesPairList getConcurrentPermissiveList(IntersectionRegion intersectionKey) {
        var defaultList = concurrentPermissiveList != null ? concurrentPermissiveList : new ConnectedLanesPairList();
        return getIntersectionValue(intersectionKey, concurrentPermissiveListMap, defaultList);
    }
}
