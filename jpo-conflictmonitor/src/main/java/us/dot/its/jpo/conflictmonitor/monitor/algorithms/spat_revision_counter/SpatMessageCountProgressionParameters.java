package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_revision_counter;

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
@ConfigurationProperties(prefix = "spat.revision.counter")
@ConfigDataClass
public class SpatMessageCountProgressionParameters {
    
    @ConfigData(key = "spat.revision.counter.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "spat.revision.counter.spatInputTopicName", 
        description = "The name of the topic to read SPATs from", 
        updateType = READ_ONLY)
    String spatInputTopicName;

    @ConfigData(key = "spat.revision.counter.spatRevisionEventOutputTopicName", 
        description = "The name of the topic to write SPAT revision counter events to", 
        updateType = READ_ONLY)
    String spatRevisionEventOutputTopicName;
}
