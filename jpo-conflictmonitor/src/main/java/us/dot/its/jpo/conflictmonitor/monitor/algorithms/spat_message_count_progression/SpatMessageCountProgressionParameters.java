package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression;

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
@ConfigurationProperties(prefix = "spat.message.count.progression")
@ConfigDataClass
public class SpatMessageCountProgressionParameters {
    
    @ConfigData(key = "spat.message.count.progression.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "spat.message.count.progression.spatInputTopicName", 
        description = "The name of the topic to read SPATs from", 
        updateType = READ_ONLY)
    String spatInputTopicName;

    @ConfigData(key = "spat.message.count.progression.spatMessageCountProgressionEventOutputTopicName", 
        description = "The name of the topic to write SPAT Message Count Progression events to", 
        updateType = READ_ONLY)
    String spatMessageCountProgressionEventOutputTopicName;

    @ConfigData(key = "spat.message.count.progression.processedSpatStateStoreName",
        description = "Name of the versioned state store for the jitter buffer",
        updateType = READ_ONLY)
    volatile String processedSpatStateStoreName;

    @ConfigData(key = "spat.message.count.progression.latestSpatStateStoreName",
    description = "Name of key-value store to keep track of the latest spat",
        updateType = READ_ONLY)
    volatile String latestSpatStateStoreName;

    @ConfigData(key = "spat.message.count.progression.bufferTimeMs",
        description = "The size of the spat buffer.  Must be larger than the expected interval between spats and expected jitter time.",
        units = UnitsEnum.MILLISECONDS,
        updateType = READ_ONLY)
    volatile int bufferTimeMs;

    @ConfigData(key = "spat.message.count.progression.bufferGracePeriodMs",
        description = "The grace period to allow late out-of-order spats to arrive before checking for transition events. Must be less than bufferTimeMs.",
        units = UnitsEnum.MILLISECONDS,
        updateType = READ_ONLY)
    volatile int bufferGracePeriodMs;

}
