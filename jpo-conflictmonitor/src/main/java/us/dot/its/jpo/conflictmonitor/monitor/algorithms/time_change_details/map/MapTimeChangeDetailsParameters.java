package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.map;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;

import org.springframework.boot.context.properties.ConfigurationProperties;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "map.time.change.details")
@ConfigDataClass
public class MapTimeChangeDetailsParameters {
 
    // Whether to log diagnostic information for debugging
    @ConfigData(key = "map.time.change.details.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;
   
}
