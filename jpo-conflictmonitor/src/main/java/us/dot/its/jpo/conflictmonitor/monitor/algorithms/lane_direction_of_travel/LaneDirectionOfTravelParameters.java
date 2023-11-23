package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUtil.getIntersectionValue;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;


import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "lane.direction.of.travel")
@ConfigDataClass
public class LaneDirectionOfTravelParameters {

    @ConfigData(key = "lane.direction.of.travel.minimumPointsPerSegment", 
        description = "The minimum number of BSM's that must be seen in a segment for that segment to be reported", 
        updateType = INTERSECTION)
    int minimumPointsPerSegment; // The minimum number of BSM's that must be seen in a segment for that segment to be reported
    
    @ConfigData(key = "lane.direction.of.travel.minimumSpeedThreshold", 
        description = "The minimum speed of the vehicle for the vehicle to be included in a segment", 
        units = MILES_PER_HOUR,
        updateType = INTERSECTION)
    double minimumSpeedThreshold; // The minimum speed of the vehicle for the vehicle to be included in a segment

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "lane.direction.of.travel.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;

    // Maps for parameters that can be customized per intersection
    final ConfigMap<Integer> minimumPointsPerSegmentMap = new ConfigMap<>();
    final ConfigMap<Double> minimumSpeedThresholdMap = new ConfigMap<>();
     
    // Intersection-specific parameters
    public int getMinimumPointsPerSegment(IntersectionRegion intersectionKey) {
        return getIntersectionValue(intersectionKey, minimumPointsPerSegmentMap, minimumPointsPerSegment);
    }
    public double getMinimumSpeedThreshold(IntersectionRegion intersectionKey) {
        return getIntersectionValue(intersectionKey, minimumSpeedThresholdMap, minimumSpeedThreshold);
    }
   
}
