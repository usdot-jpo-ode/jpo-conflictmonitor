package us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment;

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
@ConfigurationProperties(prefix = "stop.line.passage.assessment")
@ConfigDataClass
public class StopLinePassageAssessmentParameters {

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "stop.line.passage.assessment.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "stop.line.passage.assessment.stopLinePassageEventTopicName", 
        description = "The name of the topic to read Stop Line Passage Events from", 
        updateType = READ_ONLY)
    String stopLinePassageEventTopicName;

    @ConfigData(key = "stop.line.passage.assessment.stopLinePassageAssessmentOutputTopicName", 
        description = "The name of the topic to write Stop Line Passage Event Assessments to", 
        updateType = READ_ONLY)
    String stopLinePassageAssessmentOutputTopicName;

    @ConfigData(key = "stop.line.passage.assessment.stopLinePassageNotificationOutputTopicName", 
        description = "The name of the topic to write Stop Line Passage Event Assessments to", 
        updateType = READ_ONLY)
    String stopLinePassageNotificationOutputTopicName;

    @ConfigData(key = "stop.line.passage.assessment.lookBackPeriodDays", 
        description = "The number of days to look back for Stop Line Passage Events", 
        units = DAYS,
        updateType = DEFAULT)
    long lookBackPeriodDays;

    @ConfigData(key = "stop.line.passage.assessment.lookBackPeriodGraceTimeSeconds", 
        description = "The look back grace period for Stop Line Passage Events", 
        units = SECONDS,
        updateType = DEFAULT)
    long lookBackPeriodGraceTimeSeconds;

    @ConfigData(key = "stop.line.passage.assessment.minimumEventsToNotify", 
        units = PER_PERIOD, 
        description = "Minimum Number of Events to occur in a given period before a notification can be triggered", 
        updateType = DEFAULT)
    int minimumEventsToNotify;

    @ConfigData(key = "stop.line.passage.assessment.redLightToNotify", 
        units = PER_PERIOD, 
        description = "The percent of instances the light must be red in order to trigger a notification", 
        updateType = DEFAULT)
    double redLightPercentToNotify;

    @ConfigData(key = "stop.line.passage.assessment.yellowLightToNotify", 
        units = PER_PERIOD, 
        description = "The percent of instances the light must be yellow or red in order to trigger a notification", 
        updateType = DEFAULT)
    double yellowLightPercentToNotify;

}
