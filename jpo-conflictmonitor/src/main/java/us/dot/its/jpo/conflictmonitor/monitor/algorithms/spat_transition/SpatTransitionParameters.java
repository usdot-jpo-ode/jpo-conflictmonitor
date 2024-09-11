package us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum;
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

    @ConfigData(key = "spat.transition.signalGroupStateStoreName",
            description = "Name of the versioned state store for the spat buffer",
            updateType = READ_ONLY)
    volatile String movementStateStoreName;

    @ConfigData(key = "spat.transition.bufferTimeMs",
        description = "The size of the spat buffer.  Must be larger than the expected interval between spats and expected jitter time.",
        units = UnitsEnum.MILLISECONDS,
        updateType = READ_ONLY)
    volatile int bufferTimeMs;

    @ConfigData(key = "spat.transition.bufferGracePeriodMs",
        description = """
            The grace period to allow late out-of-order spats to arrive before checking for transition events.
            Must be smaller than the buffer size.""",
        units = UnitsEnum.MILLISECONDS,
        updateType = READ_ONLY)
    volatile int bufferGracePeriodMs;

//    @ConfigData(key = "spat.transition.keyStoreName",
//            description = "Name of the key state store",
//            updateType = READ_ONLY)
//    volatile String keyStoreName;

    // Do we want to make this configurable at the intersection and signal-group level?
    // For now it is not.
    @ConfigData(key = "spat.transition.illegalSpatTransitionList",
        description = "List of signal state transitions that are illegal and should produce an event",
        updateType = READ_ONLY)
    volatile PhaseStateTransitionList illegalSpatTransitionList;
}
