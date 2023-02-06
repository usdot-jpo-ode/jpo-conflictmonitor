package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;


@Component
@PropertySource("classpath:spatTimeChangeDetails-${spat.time.change.details.properties}.properties")
public class SpatTimeChangeDetailsParameters {
    

    // Whether to log diagnostic information for debugging
    boolean debug;
    String spatInputTopicName;
    String spatTimeChangeDetailsTopicName;
    String spatTimeChangeDetailsStateStoreName;
    int jitterBufferSize;
    String spatTimeChangeDetailsNotificationTopicName;
    

    public boolean isDebug() {
        return this.debug;
    }

    @Value("${spat.time.change.details.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getSpatInputTopicName() {
        return spatInputTopicName;
    }

    @Value("${spat.time.change.details.spatInputTopicName}")
    public void setSpatInputTopicName(String spatInputTopicName) {
        this.spatInputTopicName = spatInputTopicName;
    }

    public String getSpatOutputTopicName() {
        return spatTimeChangeDetailsTopicName;
    }

    @Value("${spat.time.change.details.spatTimeChangeDetailsTopicName}")
    public void setSpatOutputTopicName(String spatTimeChangeDetailsTopicName) {
        this.spatTimeChangeDetailsTopicName = spatTimeChangeDetailsTopicName;
    }

    public String getSpatTimeChangeDetailsStateStoreName() {
        return spatTimeChangeDetailsStateStoreName;
    }

    @Value("${spat.time.change.details.spatTimeChangeDetailsStateStoreName}")
    public void setSpatTimeChangeDetailsStateStoreName(String spatTimeChangeDetailsStateStoreName) {
        this.spatTimeChangeDetailsStateStoreName = spatTimeChangeDetailsStateStoreName;
    }

    public int getJitterBufferSize() {
        return jitterBufferSize;
    }

    @Value("${spat.time.change.details.jitterBufferSize}")
    public void setJitterBufferSize(int jitterBufferSize) {
        this.jitterBufferSize = jitterBufferSize;
    }

    public String getSpatTimeChangeDetailsNotificationTopicName() {
        return spatTimeChangeDetailsNotificationTopicName;
    }

    @Value("${spat.time.change.details.spatTimeChangeDetailsNotificationTopicName}")
    public void setSpatTimeChangeDetailsNotificationTopicName(String spatTimeChangeDetailsNotificationTopicName) {
        this.spatTimeChangeDetailsNotificationTopicName = spatTimeChangeDetailsNotificationTopicName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SpatTimeChangeDetailsParameters)) {
            return false;
        }
        SpatTimeChangeDetailsParameters spatBroadcastRateParameters = (SpatTimeChangeDetailsParameters) o;
        return spatInputTopicName.equals(spatBroadcastRateParameters.spatInputTopicName) && debug == spatBroadcastRateParameters.debug;
    }

    @Override
    public int hashCode() {
        return Objects.hash(spatInputTopicName, debug);
    }


    @Override
    public String toString() {
        return "{" +
            ", debug='" + isDebug() + "'" +
            ", spatInputTopicName='" + getSpatInputTopicName() + "'" +
            "}";
    }
    
}
