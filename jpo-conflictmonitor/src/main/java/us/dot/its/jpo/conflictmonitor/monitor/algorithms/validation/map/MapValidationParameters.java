package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "map.validation")
public class MapValidationParameters {
    
    String inputTopicName;

    /**
     * Output topic for 'Broadcast Rate' events
     */
    String broadcastRateTopicName;

    /**
     * Output topc for 'Minimum Data' events
     */
    String minimumDataTopicName;

    // Window parameters
    int rollingPeriodSeconds;
    int outputIntervalSeconds;
    int gracePeriodMilliseconds;

    // Exclusive min and max to send broadcast rateq events
    int lowerBound;
    int upperBound;

    // Whether to log diagnostic information for debugging
    boolean debug;
   
}
