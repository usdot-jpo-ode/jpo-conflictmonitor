package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "spat.timestamp.delta")
@ConfigDataClass
public class SpatTimestampDeltaParameters {

    @ConfigData(key = "spat.timestamp.delta.inputTopicName",
            description = "Input Kafka topic",
            updateType = READ_ONLY)
    volatile String inputTopicName;

    @ConfigData(key = "spat.timestamp.delta.outputTopicName",
            description = "Output Kafka topic",
            updateType = READ_ONLY)
    volatile String outputTopicName;

    @ConfigData(key = "spat.timestamp.delta.maxDeltaMilliseconds",
            description = "Timestamp delta above which an event is triggered",
            updateType = DEFAULT)
    volatile int maxDeltaMilliseconds;

    @ConfigData(key = "spat.timestamp.delta.debug",
            description = "Whether to log diagnostic info",
            updateType = DEFAULT)
    volatile boolean debug;

}
