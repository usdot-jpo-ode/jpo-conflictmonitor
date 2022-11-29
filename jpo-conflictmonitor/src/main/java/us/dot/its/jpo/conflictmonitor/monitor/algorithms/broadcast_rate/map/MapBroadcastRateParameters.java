package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.map;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;


@Component
@PropertySource("classpath:mapBroadcastRate-${map.broadcast.rate.properties}.properties")
public class MapBroadcastRateParameters {
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

    @Value("${map.broadcast.rate.outputEventTopicName}")
    public void setOutputEventTopicName(String outputEventTopicName) {
        this.outputEventTopicName = outputEventTopicName;
    }
    

    
    public String getInputTopicName() {
        return this.inputTopicName;
    }

    @Value("${map.broadcast.rate.inputTopicName}")
    public void setInputTopicName(String inputTopicName) {
        this.inputTopicName = inputTopicName;
    }

   
    
    public int getRollingPeriodSeconds() {
        return this.rollingPeriodSeconds;
    }

    @Value("${map.broadcast.rate.rollingPeriodSeconds}")
    public void setRollingPeriodSeconds(int rollingPeriodDurationSeconds) {
        this.rollingPeriodSeconds = rollingPeriodDurationSeconds;
    }

    
    public int getOutputIntervalSeconds() {
        return this.outputIntervalSeconds;
    }

    @Value("${map.broadcast.rate.outputIntervalSeconds}")
    public void setOutputIntervalSeconds(int outputIntervalSeconds) {
        this.outputIntervalSeconds = outputIntervalSeconds;
    }

    public int getGracePeriodMilliseconds() {
        return this.gracePeriodMilliseconds;
    }

    @Value("${map.broadcast.rate.gracePeriodMilliseconds}")
    public void setGracePeriodMilliseconds(int gracePeriodMilliseconds) {
        this.gracePeriodMilliseconds = gracePeriodMilliseconds;
    }

    public int getLowerBound() {
        return this.lowerBound;
    }

    @Value("${map.broadcast.rate.lowerBound}")
    public void setLowerBound(int broadcastRateLowerBound) {
        this.lowerBound = broadcastRateLowerBound;
    }

    
    public int getUpperBound() {
        return this.upperBound;
    }

    @Value("${map.broadcast.rate.upperBound}")
    public void setUpperBound(int broadcastRateUpperBound) {
        this.upperBound = broadcastRateUpperBound;
    }

   
    public boolean isDebug() {
        return this.debug;
    }

    @Value("${map.broadcast.rate.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MapBroadcastRateParameters)) {
            return false;
        }
        MapBroadcastRateParameters mapBroadcastRateParameters = (MapBroadcastRateParameters) o;
        return Objects.equals(inputTopicName, mapBroadcastRateParameters.inputTopicName) && Objects.equals(outputEventTopicName, mapBroadcastRateParameters.outputEventTopicName) && rollingPeriodSeconds == mapBroadcastRateParameters.rollingPeriodSeconds && outputIntervalSeconds == mapBroadcastRateParameters.outputIntervalSeconds && gracePeriodMilliseconds == mapBroadcastRateParameters.gracePeriodMilliseconds && lowerBound == mapBroadcastRateParameters.lowerBound && upperBound == mapBroadcastRateParameters.upperBound && debug == mapBroadcastRateParameters.debug;
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
