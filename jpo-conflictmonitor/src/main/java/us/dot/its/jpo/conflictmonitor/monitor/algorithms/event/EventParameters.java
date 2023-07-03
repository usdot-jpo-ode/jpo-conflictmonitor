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
    
}
