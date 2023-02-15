package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "connection.of.travel.assessment")
public class ConnectionOfTravelAssessmentParameters {
    String connectionOfTravelEventTopicName;
    String connectionOfTravelAssessmentOutputTopicName;
    long lookBackPeriodDays;
    long lookBackPeriodGraceTimeSeconds;
    String connectionOfTravelNotificationTopicName;
    int minimumNumberOfEvents;
    boolean debug;
    
}
