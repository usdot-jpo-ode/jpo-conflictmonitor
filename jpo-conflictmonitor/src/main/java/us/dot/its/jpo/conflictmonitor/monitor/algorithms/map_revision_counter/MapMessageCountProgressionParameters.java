package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_revision_counter;

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
@ConfigurationProperties(prefix = "map.revision.counter")
@ConfigDataClass
public class MapMessageCountProgressionParameters {
    
    @ConfigData(key = "map.revision.counter.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "map.revision.counter.mapInputTopicName", 
    description = "The name of the topic to read MAPs from", 
    updateType = READ_ONLY)
    String mapInputTopicName;

    @ConfigData(key = "map.revision.counter.mapRevisionEventOutputTopicName", 
        description = "The name of the topic to write map revision counter events to", 
        updateType = READ_ONLY)
    String mapRevisionEventOutputTopicName;
}
