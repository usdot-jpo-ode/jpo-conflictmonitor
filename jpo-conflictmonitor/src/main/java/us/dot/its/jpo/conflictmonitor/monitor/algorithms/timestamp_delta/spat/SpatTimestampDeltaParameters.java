package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.ITimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "spat.timestamp.delta")
@ConfigDataClass
public class SpatTimestampDeltaParameters implements ITimestampDeltaParameters {

    @ConfigData(key = "spat.timestamp.delta.algorithm",
            description = "The algorithm to use",
            updateType = READ_ONLY)
    volatile String algorithm;

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

    @ConfigData(key = "spat.timestamp.delta.keyStoreName",
            description = "Name of sate store for intersection keys with events",
            updateType = READ_ONLY)
    volatile String keyStoreName;

    @ConfigData(key = "spat.timestamp.delta.eventStoreName",
            description = "Name of versioned state store to aggregate events",
            updateType = READ_ONLY)
    volatile String eventStoreName;

    @ConfigData(key = "spat.timestamp.delta.retentionTimeMinutes",
            description = "Retention time of the event state store",
            updateType = DEFAULT)
    volatile int retentionTimeMinutes;

    @ConfigData(key = "spat.timestamp.delta.notificationTopicName",
            description = "Name of topic to send notifications to",
            updateType = READ_ONLY)
    volatile String notificationTopicName;
}
