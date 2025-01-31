package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression;

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
@ConfigurationProperties(prefix = "bsm.message.count.progression")
@ConfigDataClass
public class BsmMessageCountProgressionParameters {
    
    @ConfigData(key = "bsm.message.count.progression.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "bsm.message.count.progression.bsmInputTopicName", 
        description = "The name of the topic to read BSMs from", 
        updateType = READ_ONLY)
    String bsmInputTopicName;

    @ConfigData(key = "bsm.message.count.progression.bsmMessageCountProgressionEventOutputTopicName", 
        description = "The name of the topic to write BSM Message Count Progression events to", 
        updateType = READ_ONLY)
    String bsmMessageCountProgressionEventOutputTopicName;

    @ConfigData(key = "bsm.message.count.progression.processedBsmStateStoreName",
        description = "Name of the versioned state store for the jitter buffer",
        updateType = READ_ONLY)
    volatile String processedBsmStateStoreName;

    @ConfigData(key = "bsm.message.count.progression.latestBsmStateStoreName",
        description = "Name of key-value store to keep track of the latest BSM",
        updateType = READ_ONLY)
    volatile String latestBsmStateStoreName;

    @ConfigData(key = "bsm.message.count.progression.bufferTimeMs",
        description = "The size of the BSM buffer. Must be larger than the expected interval between BSMs and expected jitter time.",
        units = UnitsEnum.MILLISECONDS,
        updateType = READ_ONLY)
    volatile int bufferTimeMs;

    @ConfigData(key = "bsm.message.count.progression.bufferGracePeriodMs",
        description = "The grace period to allow late out-of-order BSMs to arrive before checking for transition events. Must be less than bufferTimeMs.",
        units = UnitsEnum.MILLISECONDS,
        updateType = READ_ONLY)
    volatile int bufferGracePeriodMs;

    @ConfigData(key = "bsm.message.count.progression.aggregateEvents",
        description = "Whether to aggregate events",
        updateType = READ_ONLY)
    boolean aggregateEvents;
}