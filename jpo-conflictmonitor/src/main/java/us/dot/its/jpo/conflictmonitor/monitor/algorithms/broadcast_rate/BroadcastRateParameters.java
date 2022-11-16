package us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate;

import java.util.Objects;


/**
 * Configuration parameters for Broadcast Rate algorithms
 */
public abstract class BroadcastRateParameters {
    
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

    public int getGracePeriodMilliseconds() {
        return this.gracePeriodMilliseconds;
    }

    public void setGracePeriodMilliseconds(int gracePeriodMilliseconds) {
        this.gracePeriodMilliseconds = gracePeriodMilliseconds;
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

   
    public boolean isDebug() {
        return this.debug;
    }

     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BroadcastRateParameters)) {
            return false;
        }
        BroadcastRateParameters broadcastRateParameters = (BroadcastRateParameters) o;
        return Objects.equals(inputTopicName, broadcastRateParameters.inputTopicName) && Objects.equals(outputEventTopicName, broadcastRateParameters.outputEventTopicName) && rollingPeriodSeconds == broadcastRateParameters.rollingPeriodSeconds && outputIntervalSeconds == broadcastRateParameters.outputIntervalSeconds && gracePeriodMilliseconds == broadcastRateParameters.gracePeriodMilliseconds && lowerBound == broadcastRateParameters.lowerBound && upperBound == broadcastRateParameters.upperBound && debug == broadcastRateParameters.debug;
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
