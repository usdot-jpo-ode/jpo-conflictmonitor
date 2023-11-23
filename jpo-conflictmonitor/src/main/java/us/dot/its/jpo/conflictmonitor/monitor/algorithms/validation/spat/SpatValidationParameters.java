package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.*;


import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUtil.*;


import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;


import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;


@Data
@Generated
@Component
@ConfigurationProperties(prefix = "spat.validation")
@ConfigDataClass
public class SpatValidationParameters {

    @ConfigData(key = "spat.validation.inputTopicName", 
        description = "Input kafka topic", 
        updateType = READ_ONLY)
    String inputTopicName;

    /**
     * Output topic for 'Broadcast Rate' events
     */
    @ConfigData(key = "spat.validation.broadcastRateTopicName", 
        description = "Output topic for Broadcast Rate events", 
        updateType = READ_ONLY)
    String broadcastRateTopicName;

    /**
     * Output topc for 'Minimum Data' events
     */
    @ConfigData(key = "spat.validation.minimumDataTopicName", 
        description = "Output topic for Minimum Data events", 
        updateType = READ_ONLY)
    String minimumDataTopicName;

    // Window parameters
    @ConfigData(key = "spat.validation.rollingPeriodSeconds", 
        units = SECONDS, 
        description = "The aggregation window size", 
        updateType = DEFAULT)
    int rollingPeriodSeconds;

    @ConfigData(key = "spat.validation.outputIntervalSeconds", 
        units = SECONDS, 
        description = "The window hop", 
        updateType = DEFAULT)
    int outputIntervalSeconds;

    @ConfigData(key = "spat.validation.gracePeriodMilliseconds", 
        units = MILLISECONDS, 
        description = "Window grace period", 
        updateType = DEFAULT)
    int gracePeriodMilliseconds;

    // Exclusive min and max to send broadcast rateq events
    @ConfigData(key = "spat.validation.lowerBound", 
        units = PER_PERIOD, 
        description = "Exclusive minimum counts per period", 
        updateType = INTERSECTION)
    int lowerBound;

    @ConfigData(key = "spat.validation.upperBound", 
        units = PER_PERIOD, 
        description = "Exclusive maximum counts per period", 
        updateType = INTERSECTION)
    int upperBound;

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "spat.validation.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;
   
    
    // Maps for parameters that can be customized per intersection
    final ConfigMap<Integer> lowerBoundMap = new ConfigMap<>();
    final ConfigMap<Integer> upperBoundMap = new ConfigMap<>();

    // Intersection-specific parameters
    public int getLowerBound(IntersectionRegion intersectionKey) {
        return getIntersectionValue(intersectionKey, lowerBoundMap, lowerBound);
    }
    public int getUpperBound(IntersectionRegion intersectionKey) {
        return getIntersectionValue(intersectionKey, upperBoundMap, upperBound);
    }


}
