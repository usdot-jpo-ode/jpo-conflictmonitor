package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "lane.direction.of.travel")
public class LaneDirectionOfTravelParameters {

    int minimumPointsPerSegment; // The minimum number of BSM's that must be seen in a segment for that segment to be reported
    double minimumSpeedThreshold; // The minimum speed of the vehicle for the vehicle to be included in a segment

    // Whether to log diagnostic information for debugging
    boolean debug;
     
}
