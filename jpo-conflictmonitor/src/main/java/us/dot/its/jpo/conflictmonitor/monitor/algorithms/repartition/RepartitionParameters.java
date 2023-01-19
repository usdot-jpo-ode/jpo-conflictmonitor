package us.dot.its.jpo.conflictmonitor.monitor.algorithms.repartition;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:repartition-${repartition.properties}.properties")
public class RepartitionParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;
    String bsmInputTopicName;
    String bsmRepartitionOutputTopicName;

    

    public boolean isDebug() {
        return this.debug;
    }

    @Value("${repartition.debug}")
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getBsmInputTopicName() {
        return bsmInputTopicName;
    }

    @Value("${repartition.bsmInputTopicName}")
    public void setBsmInputTopicName(String bsmInputTopicName) {
        this.bsmInputTopicName = bsmInputTopicName;
    }

    public String getBsmRepartitionOutputTopicName() {
        return bsmRepartitionOutputTopicName;
    }

    @Value("${repartition.bsmRepartitionOutputTopicName}")
    public void setBsmRepartitionOutputTopicName(String bsmRepartitionOutputTopicName) {
        this.bsmRepartitionOutputTopicName = bsmRepartitionOutputTopicName;
    }

    

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RepartitionParameters)) {
            return false;
        }
        RepartitionParameters repartitionParameters = (RepartitionParameters) o;
        return debug == repartitionParameters.debug && bsmInputTopicName.equals(bsmInputTopicName) && bsmRepartitionOutputTopicName.equals(bsmRepartitionOutputTopicName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(debug);
    }

    @Override
    public String toString() {
        return "{" +
                ", debug='" + isDebug() + "'" +
                "}";
    }
}
