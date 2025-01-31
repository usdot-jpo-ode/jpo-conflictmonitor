package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "map.message.count.progression")
@ConfigDataClass
public class MapMessageCountProgressionParameters {
    
    @ConfigData(key = "map.message.count.progression.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "map.message.count.progression.mapInputTopicName", 
        description = "The name of the topic to read MAPs from", 
        updateType = READ_ONLY)
    String mapInputTopicName;

    @ConfigData(key = "map.message.count.progression.mapMessageCountProgressionEventOutputTopicName", 
        description = "The name of the topic to write MAP Message Count Progression events to", 
        updateType = READ_ONLY)
    String mapMessageCountProgressionEventOutputTopicName;

    @ConfigData(key = "map.message.count.progression.processedMapStateStoreName",
        description = "Name of the versioned state store for the jitter buffer",
        updateType = READ_ONLY)
    volatile String processedMapStateStoreName;

    @ConfigData(key = "map.message.count.progression.latestMapStateStoreName",
        description = "Name of key-value store to keep track of the latest MAP",
        updateType = READ_ONLY)
    volatile String latestMapStateStoreName;

    @ConfigData(key = "map.message.count.progression.bufferTimeMs",
        description = "The size of the MAP buffer. Must be larger than the expected interval between MAPs and expected jitter time.",
        units = UnitsEnum.MILLISECONDS,
        updateType = READ_ONLY)
    volatile int bufferTimeMs;

    @ConfigData(key = "map.message.count.progression.bufferGracePeriodMs",
        description = "The grace period to allow late out-of-order MAPs to arrive before checking for transition events. Must be less than bufferTimeMs.",
        units = UnitsEnum.MILLISECONDS,
        updateType = READ_ONLY)
    volatile int bufferGracePeriodMs;

    @ConfigData(key = "map.message.count.progression.aggregateEvents",
        description = "Whether to aggregate events",
        updateType = READ_ONLY)
    boolean aggregateEvents;
}