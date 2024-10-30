package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_revision_counter;

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
@ConfigurationProperties(prefix = "bsm.revision.counter")
@ConfigDataClass
public class BsmMessageCountProgressionParameters {
    
    @ConfigData(key = "bsm.revision.counter.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "bsm.revision.counter.bsmInputTopicName", 
        description = "The name of the topic to read BSMs from", 
        updateType = READ_ONLY)
    String bsmInputTopicName;

    @ConfigData(key = "bsm.revision.counter.bsmRevisionEventOutputTopicName", 
        description = "The name of the topic to write BSM revision counter events to", 
        updateType = READ_ONLY)
    String bsmRevisionEventOutputTopicName;
}
