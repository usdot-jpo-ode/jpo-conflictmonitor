package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment;

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
@ConfigurationProperties(prefix = "signal.state.event.assessment")
@ConfigDataClass
public class SignalStateEventAssessmentParameters {

    // Whether to log diagnostic information for debugging
    @ConfigData(key = "signal.state.event.assessment.debug", 
        description = "Whether to log diagnostic information for debugging", 
        updateType = DEFAULT)
    boolean debug;

    @ConfigData(key = "signal.state.event.assessment.signalStateEventTopicName", 
        description = "The name of the topic to read Signal State Events from", 
        updateType = READ_ONLY)
    String signalStateEventTopicName;

    @ConfigData(key = "signal.state.event.assessment.signalStateEventAssessmentOutputTopicName", 
        description = "The name of the topic to write Signal State Event Assessments to", 
        updateType = READ_ONLY)
    String signalStateEventAssessmentOutputTopicName;

    @ConfigData(key = "signal.state.event.assessment.lookBackPeriodDays", 
        description = "The number of days to look back for Signal State Events", 
        units = DAYS,
        updateType = DEFAULT)
    long lookBackPeriodDays;

    @ConfigData(key = "signal.state.event.assessment.lookBackPeriodGraceTimeSeconds", 
        description = "The look back grace period for Signal State Events", 
        units = SECONDS,
        updateType = DEFAULT)
    long lookBackPeriodGraceTimeSeconds;

}
