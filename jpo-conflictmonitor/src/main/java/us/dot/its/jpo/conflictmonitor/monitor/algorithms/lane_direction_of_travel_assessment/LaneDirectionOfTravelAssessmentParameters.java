package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment;


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
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUtil.getIntersectionValue;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "lane.direction.of.travel.assessment")
@ConfigDataClass
public class LaneDirectionOfTravelAssessmentParameters {


    // Whether to log diagnostic information for debugging
    @ConfigData(key = "lane.direction.of.travel.assessment.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "lane.direction.of.travel.assessment.laneDirectionOfTravelEventTopicName", 
        description = "The name of the topic to which lane direction of travel events are published",
        updateType = READ_ONLY)
    String laneDirectionOfTravelEventTopicName;

    @ConfigData(key = "lane.direction.of.travel.assessment.laneDirectionOfTravelAssessmentOutputTopicName", 
        description = "The name of the topic to which lane direction of travel assessment results are published",
        updateType = READ_ONLY)
    String laneDirectionOfTravelAssessmentOutputTopicName;

    @ConfigData(key = "lane.direction.of.travel.assessment.laneDirectionOfTravelNotificationOutputTopicName", 
        description = "The name of the topic to which lane direction of travel notifications are published",
        updateType = READ_ONLY)
    String laneDirectionOfTravelNotificationOutputTopicName;

    @ConfigData(key = "lane.direction.of.travel.assessment.lookBackPeriodDays", 
        description = "The number of days to look back for lane direction of travel events", 
        units = DAYS, 
        updateType = DEFAULT)
    long lookBackPeriodDays;

    @ConfigData(key = "lane.direction.of.travel.assessment.lookBackPeriodGraceTimeSeconds", 
        description = "The grace period for look back events", 
        units = SECONDS, 
        updateType = DEFAULT)
    long lookBackPeriodGraceTimeSeconds;

    @ConfigData(key = "lane.direction.of.travel.assessment.lookBackPeriodGraceTimeToleranceSeconds", 
        description = "The heading tolerance.", 
        units = DEGREES, 
        updateType = INTERSECTION)
    double headingToleranceDegrees; 

    @ConfigData(key = "lane.direction.of.travel.assessment.distanceFromCenterlineToleranceCm", 
        description = "The distance from centerline tolerance.", 
        units = CENTIMETERS, 
        updateType = INTERSECTION)
    double distanceFromCenterlineToleranceCm;

    @ConfigData(key = "lane.direction.of.travel.assessment.minimumNumberOfEvents", 
        description = "The minimum number of events required to assess lane direction of travel", 
        updateType = DEFAULT)
    int minimumNumberOfEvents;

    // Maps for parameters that can be customized per intersection    
    final ConfigMap<Double> headingToleranceDegreesMap = new ConfigMap<>();
    final ConfigMap<Double> distanceFromCenterlineToleranceCmMap = new ConfigMap<>();


    // Intersection-specific parameters
    public double getHeadingToleranceDegrees(IntersectionRegion intersectionKey) {
        return getIntersectionValue(intersectionKey, headingToleranceDegreesMap, headingToleranceDegrees);
    }
    public double getDistanceFromCenterlineToleranceCm(IntersectionRegion intersectionKey) {
        return getIntersectionValue(intersectionKey, distanceFromCenterlineToleranceCmMap, distanceFromCenterlineToleranceCm);
    }

}
