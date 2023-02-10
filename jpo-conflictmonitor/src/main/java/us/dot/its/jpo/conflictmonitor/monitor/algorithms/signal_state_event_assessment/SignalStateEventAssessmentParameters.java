package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "signal.state.event.assessment")
public class SignalStateEventAssessmentParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;
    String signalStateEventTopicName;
    String signalStateEventAssessmentOutputTopicName;
    long lookBackPeriodDays;
    long lookBackPeriodGraceTimeSeconds;

}
