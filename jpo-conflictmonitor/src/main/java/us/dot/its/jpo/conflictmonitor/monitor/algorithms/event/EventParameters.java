package us.dot.its.jpo.conflictmonitor.monitor.algorithms.event;

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
@ConfigurationProperties(prefix = "event")
@ConfigDataClass
public class EventParameters {
    
    // Whether to log diagnostic information for debugging
    @ConfigData(key = "event.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "event.eventOutputTopicName", 
        description = "The name of the topic to output events to", 
        updateType = READ_ONLY)
    String eventOutputTopicName;


    @ConfigData(key = "event.signalStateEventTopicName", 
        description = "The name of the topic to read signal state events from", 
        updateType = READ_ONLY)
    String signalStateEventTopicName;

    @ConfigData(key = "event.spatTimeChangeDetailsTopicName", 
        description = "The name of the topic to read spat time change details events from", 
        updateType = READ_ONLY)
    String spatTimeChangeDetailsTopicName;

    @ConfigData(key = "event.spatBroadcastRateTopicName", 
        description = "The name of the topic to read spat broadcast rate events from", 
        updateType = READ_ONLY)
    String spatBroadcastRateTopicName;

    @ConfigData(key = "event.spatMinimumDataTopicName", 
        description = "The name of the topic to read spat minimum data events from", 
        updateType = READ_ONLY)
    String spatMinimumDataTopicName;

    @ConfigData(key = "event.mapBroadcastRateTopicName", 
        description = "The name of the topic to read map broadcast rate events from", 
        updateType = READ_ONLY)
    String mapBroadcastRateTopicName;

    @ConfigData(key = "event.mapMinimumDataTopicName", 
        description = "The name of the topic to read map minimum data events from", 
        updateType = READ_ONLY)
    String mapMinimumDataTopicName;

    @ConfigData(key = "event.signalGroupAlignmentEventTopicName", 
        description = "The name of the topic to read signal group alignment events from", 
        updateType = READ_ONLY)
    String signalGroupAlignmentEventTopicName;

    @ConfigData(key = "event.intersectionReferenceAlignmentEventTopicName", 
        description = "The name of the topic to read intersection reference alignment events from", 
        updateType = READ_ONLY)
    String intersectionReferenceAlignmentEventTopicName;

    @ConfigData(key = "event.signalStateConflictEventTopicName", 
        description = "The name of the topic to read signal state conflict events from", 
        updateType = READ_ONLY)
    String signalStateConflictEventTopicName;

    @ConfigData(key = "event.laneDirectionOfTravelEventTopicName", 
        description = "The name of the topic to read lane direction of travel events from", 
        updateType = READ_ONLY)
    String laneDirectionOfTravelEventTopicName;

    @ConfigData(key = "event.connectionOfTravelEventTopicName", 
        description = "The name of the topic to read connection of travel events from", 
        updateType = READ_ONLY)
    String connectionOfTravelEventTopicName;

    @ConfigData(key = "event.spatRevisionCounterEventTopicName", 
        description = "The name of the topic to read spat revision counter events from", 
        updateType = READ_ONLY)
    String spatRevisionCounterEventTopicName;

    @ConfigData(key = "event.mapRevisionCounterEventTopicName", 
        description = "The name of the topic to read map revision counter events from", 
        updateType = READ_ONLY)
    String mapRevisionCounterEventTopicName;
    
}
