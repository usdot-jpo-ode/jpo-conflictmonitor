package us.dot.its.jpo.conflictmonitor.monitor.component.broadcast_rate;

import java.util.Objects;

/**
 * Configuration parameters for Broadcast Rate algorithms
 */
public class BroadcastRateParameters {
    
    private String inputTopicName;
    private String outputTopicName;
    int rollingPeriodDurationSeconds = 10;
    int outputIntervalSeconds = 5;
    int broadcastRateLowerBound = 90;
    int broadcastRateUpperBound = 110;


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

    public int getRollingPeriodDurationSeconds() {
        return this.rollingPeriodDurationSeconds;
    }

    public void setRollingPeriodDurationSeconds(int rollingPeriodDurationSeconds) {
        this.rollingPeriodDurationSeconds = rollingPeriodDurationSeconds;
    }

    public int getOutputIntervalSeconds() {
        return this.outputIntervalSeconds;
    }

    public void setOutputIntervalSeconds(int outputIntervalSeconds) {
        this.outputIntervalSeconds = outputIntervalSeconds;
    }

    public int getBroadcastRateLowerBound() {
        return this.broadcastRateLowerBound;
    }

    public void setBroadcastRateLowerBound(int broadcastRateLowerBound) {
        this.broadcastRateLowerBound = broadcastRateLowerBound;
    }

    public int getBroadcastRateUpperBound() {
        return this.broadcastRateUpperBound;
    }

    public void setBroadcastRateUpperBound(int broadcastRateUpperBound) {
        this.broadcastRateUpperBound = broadcastRateUpperBound;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BroadcastRateParameters)) {
            return false;
        }
        BroadcastRateParameters broadcastRateParameters = (BroadcastRateParameters) o;
        return Objects.equals(inputTopicName, broadcastRateParameters.inputTopicName) && Objects.equals(outputTopicName, broadcastRateParameters.outputTopicName) && rollingPeriodDurationSeconds == broadcastRateParameters.rollingPeriodDurationSeconds && outputIntervalSeconds == broadcastRateParameters.outputIntervalSeconds && broadcastRateLowerBound == broadcastRateParameters.broadcastRateLowerBound && broadcastRateUpperBound == broadcastRateParameters.broadcastRateUpperBound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputTopicName, outputTopicName, rollingPeriodDurationSeconds, outputIntervalSeconds, broadcastRateLowerBound, broadcastRateUpperBound);
    }


    @Override
    public String toString() {
        return "{" +
            " inputTopicName='" + getInputTopicName() + "'" +
            ", outputTopicName='" + getOutputTopicName() + "'" +
            ", rollingPeriodDurationSeconds='" + getRollingPeriodDurationSeconds() + "'" +
            ", outputIntervalSeconds='" + getOutputIntervalSeconds() + "'" +
            ", broadcastRateLowerBound='" + getBroadcastRateLowerBound() + "'" +
            ", broadcastRateUpperBound='" + getBroadcastRateUpperBound() + "'" +
            "}";
    }


}
