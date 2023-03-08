package us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition;

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
@ConfigurationProperties(prefix = "repartition")
@ConfigDataClass
public class RepartitionParameters {
    
    // Whether to log diagnostic information for debugging
    @ConfigData(key = "repartition.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "repartition.bsmInputTopicName", 
        description = "The name of the topic to read BSMs from", 
        updateType = READ_ONLY)
    String bsmInputTopicName;

    @ConfigData(key = "repartition.bsmRepartitionOutputTopicName", 
        description = "The name of the topic to write repartitioned BSMs to", 
        updateType = READ_ONLY)
    String bsmRepartitionOutputTopicName;

}
