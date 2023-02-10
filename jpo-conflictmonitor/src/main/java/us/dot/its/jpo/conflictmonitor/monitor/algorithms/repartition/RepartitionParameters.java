package us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "repartition")
public class RepartitionParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;
    String bsmInputTopicName;
    String bsmRepartitionOutputTopicName;

}
