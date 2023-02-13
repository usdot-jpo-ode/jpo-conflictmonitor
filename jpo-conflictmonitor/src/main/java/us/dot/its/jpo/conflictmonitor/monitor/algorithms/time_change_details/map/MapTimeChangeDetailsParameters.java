package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.map;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "map.time.change.details")
public class MapTimeChangeDetailsParameters {
 
    // Whether to log diagnostic information for debugging
    boolean debug;
   
}
