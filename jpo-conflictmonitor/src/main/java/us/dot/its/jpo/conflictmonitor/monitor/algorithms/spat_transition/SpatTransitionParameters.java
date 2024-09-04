package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.PhaseStateTransitionList;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "spat.transition")
@ConfigDataClass
public class SpatTransitionParameters {

    @ConfigData(key = "spat.transition.algorithm",
        description = "Name of the specific algorithm",
        updateType = READ_ONLY)
    volatile String algorithm;

    @ConfigData(key = "spat.transition.debug",
        description = "Whether to log diagnostic info",
        updateType = DEFAULT)
    volatile boolean debug;

    @ConfigData(key = "spat.transition.outputTopicName",
            description = "Name of the output topic",
            updateType = READ_ONLY)
    volatile String outputTopicName;

    @ConfigData(key = "spat.transition.notificationTopicName",
            description = "Name of the notification topic",
            updateType = READ_ONLY)
    volatile String notificationTopicName;

    @ConfigData(key = "spat.transition.stateStoreName",
            description = "Name of the state store for the processor",
            updateType = READ_ONLY)
    volatile String stateStoreName;

    // Do we want to make this configurable at the intersection and signal-group level?
    // For now it is not.
    @ConfigData(key = "spat.transition.illegalSpatTransitionList",
        description = "List of signal state transitions that are illegal and should produce an event",
        updateType = READ_ONLY)
    volatile PhaseStateTransitionList illegalSpatTransitionList;
}
