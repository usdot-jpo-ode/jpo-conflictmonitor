package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "stop.line.stop.assessment")
@ConfigDataClass
public class StopLineStopAssessmentParameters {

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "stop.line.stop.assessment.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "stop.line.stop.assessment.stopLineStopEventTopicName", 
        description = "The name of the topic to read Stop Line Passage Events from", 
        updateType = READ_ONLY)
    String stopLineStopEventTopicName;

    @ConfigData(key = "stop.line.stop.assessment.stopLineStopAssessmentOutputTopicName", 
        description = "The name of the topic to write Stop Line Passage Event Assessments to", 
        updateType = READ_ONLY)
    String stopLineStopAssessmentOutputTopicName;

    @ConfigData(key = "stop.line.stop.assessment.stopLineStopNotificationOutputTopicName", 
        description = "The name of the topic to write Stop Line Stop Notification to", 
        updateType = READ_ONLY)
    String stopLineStopNotificationOutputTopicName;

    @ConfigData(key = "stop.line.stop.assessment.lookBackPeriodDays", 
        description = "The number of days to look back for Stop Line Passage Events", 
        units = DAYS,
        updateType = DEFAULT)
    long lookBackPeriodDays;

    @ConfigData(key = "stop.line.stop.assessment.lookBackPeriodGraceTimeSeconds", 
        description = "The look back grace period for Stop Line Passage Events", 
        units = SECONDS,
        updateType = DEFAULT)
    long lookBackPeriodGraceTimeSeconds;


    @ConfigData(key = "stop.line.stop.assessment.minimumEventsToNotify", 
        units = PER_PERIOD, 
        description = "Minimum Number of Events to occur in a given period before a notification can be triggered", 
        updateType = DEFAULT)
    int minimumEventsToNotify;

    @ConfigData(key = "stop.line.stop.assessment.greenLightPercentToNotify", 
        units = PER_PERIOD, 
        description = "The percent of time the light must be green in order to trigger a notification", 
        updateType = DEFAULT)
    double greenLightPercentToNotify;

}
