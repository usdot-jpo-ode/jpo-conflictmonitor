package us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;


@Data
@Generated
@Component
@PropertySource(name = "spatValidation", value = "classpath:spatValidation-${spat.validation.properties}.properties")
public class SpatValidationParameters {

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
   


    @Value("${spat.validation.broadcastRateTopicName}")
    public void setBroadcastRateTopicName(String broadcastRateTopicName) {
        this.broadcastRateTopicName = broadcastRateTopicName;
    }

    @Value("${spat.validation.minimumDataTopicName}")
    public void setMinimumDataTopicName(String minimumDataTopicName) {
        this.minimumDataTopicName = minimumDataTopicName;
    }


    @Value("${spat.validation.inputTopicName}")
    public void setInputTopicName(String inputTopicName) {
        this.inputTopicName = inputTopicName;
    }

    @Value("${spat.validation.rollingPeriodSeconds}")
    public void setRollingPeriodSeconds(int rollingPeriodDurationSeconds) {
        this.rollingPeriodSeconds = rollingPeriodDurationSeconds;
    }

    @Value("${spat.validation.outputIntervalSeconds}")
    public void setOutputIntervalSeconds(int outputIntervalSeconds) {
        this.outputIntervalSeconds = outputIntervalSeconds;
    }

    @Value("${spat.validation.gracePeriodMilliseconds}")
    public void setGracePeriodMilliseconds(int gracePeriodMilliseconds) {
        this.gracePeriodMilliseconds = gracePeriodMilliseconds;
    }

    @Value("${spat.validation.lowerBound}")
    public void setLowerBound(int broadcastRateLowerBound) {
        this.lowerBound = broadcastRateLowerBound;
    }

    @Value("${spat.validation.upperBound}")
    public void setUpperBound(int broadcastRateUpperBound) {
        this.upperBound = broadcastRateUpperBound;
    }

    @Value("${spat.validation.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
