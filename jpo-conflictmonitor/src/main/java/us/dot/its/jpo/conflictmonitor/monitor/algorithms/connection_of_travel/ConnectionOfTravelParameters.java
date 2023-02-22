package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel;

import lombok.Data;
import lombok.Generated;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "connection.of.travel")
public class ConnectionOfTravelParameters {

    // Whether to log diagnostic information for debugging
    boolean debug;
   
}
