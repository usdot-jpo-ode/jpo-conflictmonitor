package us.dot.its.jpo.conflictmonitor.monitor.algorithms.assessment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigData;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigDataClass;
import static us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType.*;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "assessment")
@ConfigDataClass
public class AssessmentParameters {
    
    // Whether to log diagnostic information for debugging
    @ConfigData(key = "assessment.debug", 
        description = "Whether to log diagnostic information for debugging",
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "assessment.assessmentOutputTopicName", 
        description = "The name of the topic to output assessments to", 
        updateType = READ_ONLY)
    String assessmentOutputTopicName;

    @ConfigData(key = "topic.CmLaneDirectionOfTravelAssessment", 
        description = "The name of the topic to read Lane Direction of Travel assessments from", 
        updateType = READ_ONLY)
    String laneDirectionOfTravelAssessmentTopicName;

    @ConfigData(key = "assessment.connectionOfTravelAssessmentTopicName", 
        description = "The name of the topic to read Connection of Travel assessments from", 
        updateType = READ_ONLY)
    String connectionOfTravelAssessmentTopicName;


    @ConfigData(key = "assessment.signalStateEventAssessmentTopicName", 
        description = "The name of the topic to read Signal state Event Assessments from", 
        updateType = READ_ONLY)
    String signalStateEventAssessmentTopicName;
    
}
