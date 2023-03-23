package us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event;

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
@ConfigurationProperties(prefix = "bsm.event")
@ConfigDataClass
public class BsmEventParameters {
    
    @ConfigData(key = "bsm.event.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "bsm.event.inputTopic", 
        description = "The Kafka topic to read BSMs from",
        updateType = READ_ONLY)
    String inputTopic;

    @ConfigData(key = "bsm.event.outputTopic", 
        description = "The Kafka topic to write BSM events to",
        updateType = READ_ONLY)
    String outputTopic;

    @ConfigData(key = "bsm.event.stateStoreName", 
        description = "The name of the Timestamped KeyValue Store for BSMs",
        updateType = READ_ONLY)
    String stateStoreName;
}
