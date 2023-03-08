package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment;

import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.DAYS;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.PER_PERIOD;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.SECONDS;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.DEFAULT;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.READ_ONLY;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "connection.of.travel.assessment")
@ConfigDataClass
public class ConnectionOfTravelAssessmentParameters {

    @ConfigData(key = "connection.of.travel.assessment.connectionOfTravelEventTopicName", 
        description = "Input kafka topic",
        updateType = READ_ONLY)
    String connectionOfTravelEventTopicName;

    @ConfigData(key = "connection.of.travel.assessment.connectionOfTravelAssessmentOutputTopicName", 
        description = "Output kafka topic",
        updateType = READ_ONLY)
    String connectionOfTravelAssessmentOutputTopicName;

    @ConfigData(key = "connection.of.travel.assessment.connectionOfTravelNotificationTopicName", 
        description = "Output kafka topic for connection of travel notifications",
        updateType = READ_ONLY)
    String connectionOfTravelNotificationTopicName;

    @ConfigData(key = "connection.of.travel.assessment.lookBackPeriodDays", 
        description = "Look back period in days", 
        units = DAYS, 
        updateType = DEFAULT)
    long lookBackPeriodDays;

    @ConfigData(key = "connection.of.travel.assessment.lookBackPeriodGraceTimeSeconds", 
        description = "Look back period grace time in seconds", 
        units = SECONDS, 
        updateType = DEFAULT)
    long lookBackPeriodGraceTimeSeconds;   

    @ConfigData(key = "connection.of.travel.assessment.minimumNumberOfEvents", 
        description = "Minimum number of events to trigger a connection of travel assessment", 
        units = PER_PERIOD, 
        updateType = DEFAULT)
    int minimumNumberOfEvents;

    @ConfigData(key = "connection.of.travel.assessment.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;


   
}
