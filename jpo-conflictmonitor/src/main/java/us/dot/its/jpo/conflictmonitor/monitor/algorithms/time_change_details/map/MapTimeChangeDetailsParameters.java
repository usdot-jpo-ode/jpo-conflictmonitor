package us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;


@Component
@PropertySource("classpath:mapTimeChangeDetails-${map.time.change.details.properties}.properties")
public class MapTimeChangeDetailsParameters {
    

    // Whether to log diagnostic information for debugging
    boolean debug;
   
    public boolean isDebug() {
        return this.debug;
    }

    @Value("${map.time.change.details.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MapTimeChangeDetailsParameters)) {
            return false;
        }
        MapTimeChangeDetailsParameters mapBroadcastRateParameters = (MapTimeChangeDetailsParameters) o;
        return debug == mapBroadcastRateParameters.debug;
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
