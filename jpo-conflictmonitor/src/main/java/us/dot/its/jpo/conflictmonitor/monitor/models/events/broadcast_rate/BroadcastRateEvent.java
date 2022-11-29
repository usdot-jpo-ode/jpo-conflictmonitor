package us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate;

import java.util.Objects;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;

/**
 * Base class for a Broadcast Rate event to report counts of events during the processing time period.
 */
public abstract class BroadcastRateEvent {
        
    private String topicName;
    private String sourceDeviceId;
    private ProcessingTimePeriod timePeriod;
    private int numberOfMessages;

    /**
     * @return The name of the Kafka topic containing messages to be counted
     */
    public String getTopicName() {
        return this.topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }


    /**
     * @return Get the source RSU Device ID
     */
    public String getSourceDeviceId() {
        return this.sourceDeviceId;
    }

    /**
     * @param sourceDeviceId Source RSU Device ID
     */
    public void setSourceDeviceId(String sourceDeviceId) {
        this.sourceDeviceId = sourceDeviceId;
    }


    /**
     * @return The message processing time period.
     */
    public ProcessingTimePeriod getTimePeriod() {
        return this.timePeriod;
    }

    /**
     * @param timePeriod The message processing time period.
     */
    public void setTimePeriod(ProcessingTimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }
    

    /**
     * @return The number of messages processed
     */
    public int getNumberOfMessages() {
        return this.numberOfMessages;
    }

    /**
     * @param numberOfMessages The number of messages processed
     */
    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }



    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BroadcastRateEvent)) {
            return false;
        }
        BroadcastRateEvent broadcastRateEvent = (BroadcastRateEvent) o;
        return Objects.equals(sourceDeviceId, broadcastRateEvent.sourceDeviceId) && Objects.equals(timePeriod, broadcastRateEvent.timePeriod) && numberOfMessages == broadcastRateEvent.numberOfMessages;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceDeviceId, timePeriod, numberOfMessages);
    }


    @Override
    public String toString() {
        return "{" +
            " sourceDeviceId='" + getSourceDeviceId() + "'" +
            ", timePeriod='" + getTimePeriod() + "'" +
            ", numberOfMessages='" + getNumberOfMessages() + "'" +
            "}";
    }
    
}
