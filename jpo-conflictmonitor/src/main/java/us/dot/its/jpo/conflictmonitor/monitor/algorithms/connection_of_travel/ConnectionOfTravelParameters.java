package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "connection.of.travel")
@ConfigDataClass
public class ConnectionOfTravelParameters {

    @ConfigData(key = "connection.of.travel.algorithm",
        description = "Algorithm to use for connection of travel",
        updateType = READ_ONLY)
    String algorithm;

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "connection.of.travel.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;

    
}
