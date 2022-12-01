package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;


@Component
@PropertySource("classpath:connectionOfTravel-${connection.of.travel.properties}.properties")
public class ConnectionOfTravelParameters {

    // Whether to log diagnostic information for debugging
    boolean debug;
   
    public boolean isDebug() {
        return this.debug;
    }

    @Value("${connection.of.travel.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ConnectionOfTravelParameters)) {
            return false;
        }
        ConnectionOfTravelParameters connectionOfTravelParameters = (ConnectionOfTravelParameters) o;
        return debug == connectionOfTravelParameters.debug;
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
