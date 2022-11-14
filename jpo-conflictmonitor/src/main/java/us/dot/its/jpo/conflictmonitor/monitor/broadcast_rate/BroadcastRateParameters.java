package us.dot.its.jpo.conflictmonitor.monitor.broadcast_rate;

import java.util.Objects;

/**
 * Configuration parameters for Broadcast Rate algorithms
 */
public class BroadcastRateParameters {
    
    String inputTopicName;
    String outputTopicName;
    int rollingPeriodSeconds;
    int outputIntervalSeconds;
    int lowerBound;
    int upperBound;


    public String getInputTopicName() {
        return this.inputTopicName;
    }

    public void setInputTopicName(String inputTopicName) {
        this.inputTopicName = inputTopicName;
    }

    public String getOutputTopicName() {
        return this.outputTopicName;
    }

    public void setOutputTopicName(String outputTopicName) {
        this.outputTopicName = outputTopicName;
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
        return Objects.equals(inputTopicName, broadcastRateParameters.inputTopicName) && Objects.equals(outputTopicName, broadcastRateParameters.outputTopicName) && rollingPeriodSeconds == broadcastRateParameters.rollingPeriodSeconds && outputIntervalSeconds == broadcastRateParameters.outputIntervalSeconds && lowerBound == broadcastRateParameters.lowerBound && upperBound == broadcastRateParameters.upperBound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputTopicName, outputTopicName, rollingPeriodSeconds, outputIntervalSeconds, lowerBound, upperBound);
    }


    @Override
    public String toString() {
        return "{" +
            " inputTopicName='" + getInputTopicName() + "'" +
            ", outputTopicName='" + getOutputTopicName() + "'" +
            ", rollingPeriodDurationSeconds='" + getRollingPeriodSeconds() + "'" +
            ", outputIntervalSeconds='" + getOutputIntervalSeconds() + "'" +
            ", broadcastRateLowerBound='" + getLowerBound() + "'" +
            ", broadcastRateUpperBound='" + getUpperBound() + "'" +
            "}";
    }


}
