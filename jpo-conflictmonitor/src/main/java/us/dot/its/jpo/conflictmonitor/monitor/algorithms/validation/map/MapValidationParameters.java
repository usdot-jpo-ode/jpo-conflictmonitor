package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

import org.springframework.beans.factory.annotation.Value;

@Data
@Generated
@Component
@PropertySource("classpath:mapValidation-${map.validation.properties}.properties")
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

    
 
    @Value("${map.validation.broadcastRateTopicName}")
    public void setBroadcastRateTopicName(String broadcastRateTopicName) {
        this.broadcastRateTopicName = broadcastRateTopicName;
    }

    @Value("${map.validation.minimumDataTopicName}")
    public void setMinimumDataTopicName(String minimumDataTopicName) {
        this.minimumDataTopicName = minimumDataTopicName;
    }
    

    @Value("${map.validation.inputTopicName}")
    public void setInputTopicName(String inputTopicName) {
        this.inputTopicName = inputTopicName;
    }

   

    @Value("${map.validation.rollingPeriodSeconds}")
    public void setRollingPeriodSeconds(int rollingPeriodDurationSeconds) {
        this.rollingPeriodSeconds = rollingPeriodDurationSeconds;
    }

    
    @Value("${map.validation.outputIntervalSeconds}")
    public void setOutputIntervalSeconds(int outputIntervalSeconds) {
        this.outputIntervalSeconds = outputIntervalSeconds;
    }


    @Value("${map.validation.gracePeriodMilliseconds}")
    public void setGracePeriodMilliseconds(int gracePeriodMilliseconds) {
        this.gracePeriodMilliseconds = gracePeriodMilliseconds;
    }


    @Value("${map.validation.lowerBound}")
    public void setLowerBound(int broadcastRateLowerBound) {
        this.lowerBound = broadcastRateLowerBound;
    }

    
    @Value("${map.validation.upperBound}")
    public void setUpperBound(int broadcastRateUpperBound) {
        this.upperBound = broadcastRateUpperBound;
    }

   
    @Value("${map.validation.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    
}
