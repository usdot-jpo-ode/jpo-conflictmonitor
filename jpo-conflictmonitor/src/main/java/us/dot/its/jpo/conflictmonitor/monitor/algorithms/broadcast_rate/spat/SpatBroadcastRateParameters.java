package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.spat;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;



@Component
@PropertySource(name = "spatBroadcastRate", value = "classpath:spatBroadcastRate-${spat.broadcast.rate.properties}.properties")
public class SpatBroadcastRateParameters {

    String inputTopicName;
    String outputEventTopicName;

    // Window parameters
    int rollingPeriodSeconds;
    int outputIntervalSeconds;
    int gracePeriodMilliseconds;

    // Exclusive min and max to send events
    int lowerBound;
    int upperBound;

    // Whether to log diagnostic information for debugging
    boolean debug;

    
   
    
    
    public String getOutputEventTopicName() {
        return this.outputEventTopicName;
    }

    @Value("${spat.broadcast.rate.outputEventTopicName}")
    public void setOutputEventTopicName(String outputEventTopicName) {
        this.outputEventTopicName = outputEventTopicName;
    }
    

    
    public String getInputTopicName() {
        return this.inputTopicName;
    }

    @Value("${spat.broadcast.rate.inputTopicName}")
    public void setInputTopicName(String inputTopicName) {
        this.inputTopicName = inputTopicName;
    }

   
    
    public int getRollingPeriodSeconds() {
        return this.rollingPeriodSeconds;
    }

    @Value("${spat.broadcast.rate.rollingPeriodSeconds}")
    public void setRollingPeriodSeconds(int rollingPeriodDurationSeconds) {
        this.rollingPeriodSeconds = rollingPeriodDurationSeconds;
    }

    
    public int getOutputIntervalSeconds() {
        return this.outputIntervalSeconds;
    }

    @Value("${spat.broadcast.rate.outputIntervalSeconds}")
    public void setOutputIntervalSeconds(int outputIntervalSeconds) {
        this.outputIntervalSeconds = outputIntervalSeconds;
    }

    public int getGracePeriodMilliseconds() {
        return this.gracePeriodMilliseconds;
    }

    @Value("${spat.broadcast.rate.gracePeriodMilliseconds}")
    public void setGracePeriodMilliseconds(int gracePeriodMilliseconds) {
        this.gracePeriodMilliseconds = gracePeriodMilliseconds;
    }

    public int getLowerBound() {
        return this.lowerBound;
    }

    @Value("${spat.broadcast.rate.lowerBound}")
    public void setLowerBound(int broadcastRateLowerBound) {
        this.lowerBound = broadcastRateLowerBound;
    }

    
    public int getUpperBound() {
        return this.upperBound;
    }

    @Value("${spat.broadcast.rate.upperBound}")
    public void setUpperBound(int broadcastRateUpperBound) {
        this.upperBound = broadcastRateUpperBound;
    }

   
    public boolean isDebug() {
        return this.debug;
    }

    @Value("${spat.broadcast.rate.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SpatBroadcastRateParameters)) {
            return false;
        }
        SpatBroadcastRateParameters spatBroadcastRateParameters = (SpatBroadcastRateParameters) o;
        return Objects.equals(inputTopicName, spatBroadcastRateParameters.inputTopicName) && Objects.equals(outputEventTopicName, spatBroadcastRateParameters.outputEventTopicName) && rollingPeriodSeconds == spatBroadcastRateParameters.rollingPeriodSeconds && outputIntervalSeconds == spatBroadcastRateParameters.outputIntervalSeconds && gracePeriodMilliseconds == spatBroadcastRateParameters.gracePeriodMilliseconds && lowerBound == spatBroadcastRateParameters.lowerBound && upperBound == spatBroadcastRateParameters.upperBound && debug == spatBroadcastRateParameters.debug;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputTopicName, outputEventTopicName, rollingPeriodSeconds, outputIntervalSeconds, gracePeriodMilliseconds, lowerBound, upperBound, debug);
    }


    @Override
    public String toString() {
        return "{" +
            " inputTopicName='" + getInputTopicName() + "'" +
            ", outputEventTopicName='" + getOutputEventTopicName() + "'" +
            ", rollingPeriodSeconds='" + getRollingPeriodSeconds() + "'" +
            ", outputIntervalSeconds='" + getOutputIntervalSeconds() + "'" +
            ", gracePeriodMilliseconds='" + getGracePeriodMilliseconds() + "'" +
            ", lowerBound='" + getLowerBound() + "'" +
            ", upperBound='" + getUpperBound() + "'" +
            ", debug='" + isDebug() + "'" +
            "}";
    }
    
}
