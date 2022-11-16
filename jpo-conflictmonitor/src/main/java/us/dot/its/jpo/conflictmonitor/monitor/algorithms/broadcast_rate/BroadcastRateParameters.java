package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate;

import java.util.Objects;
import java.util.Properties;

/**
 * Configuration parameters for Broadcast Rate algorithms
 */
public abstract class BroadcastRateParameters {
    
    Properties streamsProperties;

    String inputTopicName;
    String outputCountTopicName;
    String outputEventTopicName;
    int rollingPeriodSeconds;
    int outputIntervalSeconds;
    int lowerBound;
    int upperBound;
    

    public Properties getStreamsProperties() {
        return this.streamsProperties;
    }

    public void setStreamsProperties(Properties streamsProperties) {
        this.streamsProperties = streamsProperties;
    }


    public String getOutputCountTopicName() {
        return this.outputCountTopicName;
    }

    public void setOutputCountTopicName(String outputCountTopicName) {
        this.outputCountTopicName = outputCountTopicName;
    }

    public String getOutputEventTopicName() {
        return this.outputEventTopicName;
    }

    public void setOutputEventTopicName(String outputEventTopicName) {
        this.outputEventTopicName = outputEventTopicName;
    }
    


    public String getInputTopicName() {
        return this.inputTopicName;
    }

    public void setInputTopicName(String inputTopicName) {
        this.inputTopicName = inputTopicName;
    }

   

    public int getRollingPeriodSeconds() {
        return this.rollingPeriodSeconds;
    }

    public void setRollingPeriodSeconds(int rollingPeriodDurationSeconds) {
        this.rollingPeriodSeconds = rollingPeriodDurationSeconds;
    }

    public int getOutputIntervalSeconds() {
        return this.outputIntervalSeconds;
    }

    public void setOutputIntervalSeconds(int outputIntervalSeconds) {
        this.outputIntervalSeconds = outputIntervalSeconds;
    }

    public int getLowerBound() {
        return this.lowerBound;
    }

    public void setLowerBound(int broadcastRateLowerBound) {
        this.lowerBound = broadcastRateLowerBound;
    }

    public int getUpperBound() {
        return this.upperBound;
    }

    public void setUpperBound(int broadcastRateUpperBound) {
        this.upperBound = broadcastRateUpperBound;
    }




    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BroadcastRateParameters)) {
            return false;
        }
        BroadcastRateParameters broadcastRateParameters = (BroadcastRateParameters) o;
        return Objects.equals(streamsProperties, broadcastRateParameters.streamsProperties) && Objects.equals(inputTopicName, broadcastRateParameters.inputTopicName) && Objects.equals(outputCountTopicName, broadcastRateParameters.outputCountTopicName) && Objects.equals(outputEventTopicName, broadcastRateParameters.outputEventTopicName) && rollingPeriodSeconds == broadcastRateParameters.rollingPeriodSeconds && outputIntervalSeconds == broadcastRateParameters.outputIntervalSeconds && lowerBound == broadcastRateParameters.lowerBound && upperBound == broadcastRateParameters.upperBound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(streamsProperties, inputTopicName, outputCountTopicName, outputEventTopicName, rollingPeriodSeconds, outputIntervalSeconds, lowerBound, upperBound);
    }


    @Override
    public String toString() {
        return "{" +
            " streamsProperties='" + getStreamsProperties() + "'" +
            ", inputTopicName='" + getInputTopicName() + "'" +
            ", outputCountTopicName='" + getOutputCountTopicName() + "'" +
            ", outputEventTopicName='" + getOutputEventTopicName() + "'" +
            ", rollingPeriodSeconds='" + getRollingPeriodSeconds() + "'" +
            ", outputIntervalSeconds='" + getOutputIntervalSeconds() + "'" +
            ", lowerBound='" + getLowerBound() + "'" +
            ", upperBound='" + getUpperBound() + "'" +
            "}";
    }
    
    

}
