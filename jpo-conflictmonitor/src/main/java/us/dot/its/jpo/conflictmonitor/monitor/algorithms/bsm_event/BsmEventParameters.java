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

    @ConfigData(key = "bsm.event.bsmIntersectionOutputTopic",
        description = "The Kafka topic to write BSMs partitioned by intersection to",
        updateType = READ_ONLY)
    String bsmIntersectionOutputTopic;

    @ConfigData(key = "bsm.event.stateStoreName", 
        description = "The name of the Timestamped KeyValue Store for BSMs",
        updateType = READ_ONLY)
    String stateStoreName;

    @ConfigData(key = "bsm.event.algorithm", 
        description = "The algorithm to use for BSM event detection",
        updateType = READ_ONLY)
    String algorithm;

    @ConfigData(key = "bsm.event.simplifyPath",
        description = "Whether to simplify the LineString stored in the wktPath field of the BSM Event",
        updateType = DEFAULT)
    boolean simplifyPath;

    @ConfigData(key = "bsm.event.simplifyPathToleranceMeters",
        description = "The Douglas-Peucker simplification algorithm distance parameter in meters",
        updateType = DEFAULT)
    double simplifyPathToleranceMeters;
    
}
