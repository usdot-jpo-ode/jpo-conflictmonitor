package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

import org.springframework.beans.factory.annotation.Value;

@Data
@Generated
@Component
@PropertySource("classpath:mapBroadcastRate-${map.broadcast.rate.properties}.properties")
public class MapBroadcastRateParameters {
    
    String inputTopicName;

    /**
     * Output topic for 'Broadcast Rate' events
     */
    String outputEventTopicName;

    /**
     * Output topc for 'Minimum Data' events
     */
    String minimumDataEventTopicName;

    // Window parameters
    int rollingPeriodSeconds;
    int outputIntervalSeconds;
    int gracePeriodMilliseconds;

    // Exclusive min and max to send broadcast rateq events
    int lowerBound;
    int upperBound;

    // Whether to log diagnostic information for debugging
    boolean debug;

    
 
    @Value("${map.broadcast.rate.outputEventTopicName}")
    public void setOutputEventTopicName(String outputEventTopicName) {
        this.outputEventTopicName = outputEventTopicName;
    }

    @Value("${map.minimum.data.outputEventTopicName}")
    public void setMinimumDataEventTopicName(String minimumDataEventTopicName) {
        this.minimumDataEventTopicName = minimumDataEventTopicName;
    }
    

    @Value("${map.broadcast.rate.inputTopicName}")
    public void setInputTopicName(String inputTopicName) {
        this.inputTopicName = inputTopicName;
    }

   

    @Value("${map.broadcast.rate.rollingPeriodSeconds}")
    public void setRollingPeriodSeconds(int rollingPeriodDurationSeconds) {
        this.rollingPeriodSeconds = rollingPeriodDurationSeconds;
    }

    
    @Value("${map.broadcast.rate.outputIntervalSeconds}")
    public void setOutputIntervalSeconds(int outputIntervalSeconds) {
        this.outputIntervalSeconds = outputIntervalSeconds;
    }


    @Value("${map.broadcast.rate.gracePeriodMilliseconds}")
    public void setGracePeriodMilliseconds(int gracePeriodMilliseconds) {
        this.gracePeriodMilliseconds = gracePeriodMilliseconds;
    }


    @Value("${map.broadcast.rate.lowerBound}")
    public void setLowerBound(int broadcastRateLowerBound) {
        this.lowerBound = broadcastRateLowerBound;
    }

    
    @Value("${map.broadcast.rate.upperBound}")
    public void setUpperBound(int broadcastRateUpperBound) {
        this.upperBound = broadcastRateUpperBound;
    }

   
    @Value("${map.broadcast.rate.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    
}
