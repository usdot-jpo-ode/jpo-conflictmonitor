package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.*;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "spat.time.change.details")
@ConfigDataClass
public class SpatTimeChangeDetailsParameters {  

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "spat.time.change.details.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "spat.time.change.details.spatInputTopicName", 
        description = "The name of the topic to read SPAT messages from", 
        updateType = READ_ONLY)
    String spatInputTopicName;

    @ConfigData(key = "spat.time.change.details.spatTimeChangeDetailsTopicName", 
        description = "The name of the topic to write time change details to", 
        updateType = READ_ONLY)
    String spatTimeChangeDetailsTopicName;

    @ConfigData(key = "spat.time.change.details.spatTimeChangeDetailsStateStoreName", 
        description = "The name of the state store to persist time change details in", 
        updateType = READ_ONLY)
    String spatTimeChangeDetailsStateStoreName;

    @ConfigData(key = "spat.time.change.details.spatTimeChangeDetailsNotificationTopicName", 
        description = "The name of the topic to write time change details notifications to", 
        updateType = READ_ONLY)
    String spatTimeChangeDetailsNotificationTopicName;

    @ConfigData(key = "spat.time.change.details.jitterBufferSize", 
        description = "The number of SPAT messages to buffer for jitter calculation", 
        updateType = DEFAULT)
    int jitterBufferSize;

    @ConfigData(key = "spat.time.change.details.aggregateEvents",
        description = "Whether to aggregate events",
        updateType = READ_ONLY)
    boolean aggregateEvents;
    
}
