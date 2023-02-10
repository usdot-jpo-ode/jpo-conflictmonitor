package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "spat.time.change.details")
public class SpatTimeChangeDetailsParameters {  

    // Whether to log diagnostic information for debugging
    boolean debug;
    String spatInputTopicName;
    String spatTimeChangeDetailsTopicName;
    String spatTimeChangeDetailsStateStoreName;
    int jitterBufferSize;
    
}
