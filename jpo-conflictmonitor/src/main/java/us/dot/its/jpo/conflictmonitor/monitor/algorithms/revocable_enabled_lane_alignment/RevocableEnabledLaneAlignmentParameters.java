package us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment;

import lombok.Data;
import lombok.Generated;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "revocable.enabled.lane.alignment")
public class RevocableEnabledLaneAlignmentParameters {

    @ConfigData(key = "revocable.enabled.lane.alignment",
            description = "Whether to log diagnostic information for debugging",
            updateType = DEFAULT
    )
    boolean debug;
}
