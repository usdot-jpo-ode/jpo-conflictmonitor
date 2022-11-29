package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;

@Component
@PropertySource("classpath:laneDirectionOfTravel-${lane.direction.of.travel.properties}.properties")
public class LaneDirectionOfTravelParameters {



    int minimumPointsPerSegment; // The minimum number of BSM's that must be seen in a segment for that segment to be reported
    double minimumSpeedThreshold; // The minimum speed of the vehicle for the vehicle to be included in a segment

    // Whether to log diagnostic information for debugging
    boolean debug;

    
   
    public int getMinimumPointsPerSegment() {
        return minimumPointsPerSegment;
    }

    @Value("${lane.direction.of.travel.minimumPointsPerSegment}")
    public void setMinimumPointsPerSegment(int minimumPointsPerSegment) {
        this.minimumPointsPerSegment = minimumPointsPerSegment;
    }

    public double getMinimumSpeedThreshold() {
        return minimumSpeedThreshold;
    }

    @Value("${lane.direction.of.travel.minimumSpeedThreshold}")
    public void setMinimumSpeedThreshold(double minimumSpeedThreshold) {
        this.minimumSpeedThreshold = minimumSpeedThreshold;
    }
   
    public boolean isDebug() {
        return this.debug;
    }

    @Value("${lane.direction.of.travel.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LaneDirectionOfTravelParameters)) {
            return false;
        }
        LaneDirectionOfTravelParameters laneDirectionOfTravelParameters = (LaneDirectionOfTravelParameters) o;
        return Objects.equals(minimumPointsPerSegment, laneDirectionOfTravelParameters.minimumPointsPerSegment) &&
            Objects.equals(minimumSpeedThreshold, laneDirectionOfTravelParameters.minimumSpeedThreshold) && 
            debug == laneDirectionOfTravelParameters.debug;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimumPointsPerSegment, minimumSpeedThreshold, debug);
    }


    @Override
    public String toString() {
        return "{" +
            " minimumPointsPerSegment='" + getMinimumPointsPerSegment() + "'" +
            ", minimumSpeedThreshold='" + getMinimumSpeedThreshold() + "'" +
            ", debug='" + isDebug() + "'" +
            "}";
    }
    
}
