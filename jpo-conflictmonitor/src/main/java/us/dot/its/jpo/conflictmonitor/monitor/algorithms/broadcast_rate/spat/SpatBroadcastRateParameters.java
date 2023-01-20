package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;


@Data
@Generated
@Component
@PropertySource(name = "spatBroadcastRate", value = "classpath:spatBroadcastRate-${spat.broadcast.rate.properties}.properties")
public class SpatBroadcastRateParameters {

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
   


    @Value("${spat.broadcast.rate.outputEventTopicName}")
    public void setOutputEventTopicName(String outputEventTopicName) {
        this.outputEventTopicName = outputEventTopicName;
    }

    @Value("${spat.broadcast.rate.inputTopicName}")
    public void setInputTopicName(String inputTopicName) {
        this.inputTopicName = inputTopicName;
    }

    @Value("${spat.broadcast.rate.rollingPeriodSeconds}")
    public void setRollingPeriodSeconds(int rollingPeriodDurationSeconds) {
        this.rollingPeriodSeconds = rollingPeriodDurationSeconds;
    }

    @Value("${spat.broadcast.rate.outputIntervalSeconds}")
    public void setOutputIntervalSeconds(int outputIntervalSeconds) {
        this.outputIntervalSeconds = outputIntervalSeconds;
    }

    @Value("${spat.broadcast.rate.gracePeriodMilliseconds}")
    public void setGracePeriodMilliseconds(int gracePeriodMilliseconds) {
        this.gracePeriodMilliseconds = gracePeriodMilliseconds;
    }

    @Value("${spat.broadcast.rate.lowerBound}")
    public void setLowerBound(int broadcastRateLowerBound) {
        this.lowerBound = broadcastRateLowerBound;
    }

    @Value("${spat.broadcast.rate.upperBound}")
    public void setUpperBound(int broadcastRateUpperBound) {
        this.upperBound = broadcastRateUpperBound;
    }

    @Value("${spat.broadcast.rate.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
