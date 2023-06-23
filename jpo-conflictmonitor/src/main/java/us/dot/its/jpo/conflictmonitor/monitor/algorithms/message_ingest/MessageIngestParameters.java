package us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;


import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "message.ingest")
@ConfigDataClass
public class MessageIngestParameters {

    @ConfigData(key = "message.ingest.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "message.ingest.algorithm", 
        description = "The algorithm to use for BSM event detection",
        updateType = READ_ONLY)
    String algorithm;

    @ConfigData(key = "message.ingest.bsmTopic", 
        description = "The Kafka topic to read BSMs from",
        updateType = READ_ONLY)
    String bsmTopic;

    @ConfigData(key = "message.ingest.bsmStoreName", 
        description = "The name of the state store for BSMs",
        updateType = READ_ONLY)
    String bsmStoreName;

    @ConfigData(key = "message.ingest.spatTopic", 
        description = "The Kafka topic to read SPATs from",
        updateType = READ_ONLY)
    String spatTopic;

    @ConfigData(key = "message.ingest.spatStoreName", 
        description = "The name of the state store for SPATs",
        updateType = READ_ONLY)
    String spatStoreName;

    @ConfigData(key = "message.ingest.mapTopic", 
        description = "The Kafka topic to read MAPs from",
        updateType = READ_ONLY)
    String mapTopic;

    @ConfigData(key = "message.ingest.mapBoundingBoxTopic",
            description = "The Kafka topic to write the MAP bounding box to",
            updateType = READ_ONLY)
    String mapBoundingBoxTopic;

    @ConfigData(key = "message.ingest.mapStoreName", 
        description = "The name of the state store for MAPs",
        updateType = READ_ONLY)
    String mapStoreName;
    
}
