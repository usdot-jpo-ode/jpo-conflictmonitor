package us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "revocable.enabled.lane.alignment")
public class RevocableEnabledLaneAlignmentParameters {

    @ConfigData(key = "revocable.enabled.lane.alignment.debug",
            description = "Whether to log diagnostic information for debugging",
            updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "revocable.enabled.lane.alignment.aggregateEvents",
        description = "Whether to aggregate events",
        updateType = READ_ONLY)
    boolean aggregateEvents;
}
