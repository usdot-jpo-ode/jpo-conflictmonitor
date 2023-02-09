package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "lane.direction.of.travel.assessment")
public class LaneDirectionOfTravelAssessmentParameters {


    // Whether to log diagnostic information for debugging
    boolean debug;
    String laneDirectionOfTravelEventTopicName;
    String laneDirectionOfTravelAssessmentOutputTopicName;
    long lookBackPeriodDays;
    long lookBackPeriodGraceTimeSeconds;
    double headingToleranceDegrees;    
    
}
