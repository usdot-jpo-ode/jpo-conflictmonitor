package us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:signalStateEventAssessment-${signal.state.event.assessment.properties}.properties")
public class SignalStateEventAssessmentParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;
    String signalStateEventTopicName;
    String signalStateEventAssessmentOutputTopicName;
    long lookBackPeriodDays;
    long lookBackPeriodGraceTimeSeconds;

    

    public boolean isDebug() {
        return this.debug;
    }

    @Value("${signal.state.event.assessment.debug}")
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getSignalStateEventTopicName() {
        return signalStateEventTopicName;
    }

    @Value("${signal.state.event.assessment.signalStateEventTopicName}")
    public void setSignalStateEventTopicName(String signalStateEventTopicName) {
        this.signalStateEventTopicName = signalStateEventTopicName;
    }

    public String getSignalStateEventAssessmentOutputTopicName() {
        return signalStateEventAssessmentOutputTopicName;
    }

    @Value("${signal.state.event.assessment.signalStateEventAssessmentOutputTopicName}")
    public void setSignalStateEventAssessmentOutputTopicName(String signalStateEventAssessmentOutputTopicName) {
        this.signalStateEventAssessmentOutputTopicName = signalStateEventAssessmentOutputTopicName;
    }

    public long getLookBackPeriodDays() {
        return lookBackPeriodDays;
    }
    
    @Value("${signal.state.event.assessment.lookBackPeriodDays}")
    public void setLookBackPeriodDays(long lookBackPeriodDays) {
        if(lookBackPeriodDays < 1){
            System.out.println("signal.state.event.assessment.lookBackPeriodDays cannot be less than 1. Using 1 Day instead.");
            lookBackPeriodDays = 1;
        }
        this.lookBackPeriodDays = lookBackPeriodDays;
    }

    public long getLookBackPeriodGraceTimeSeconds() {
        return lookBackPeriodGraceTimeSeconds;
    }

    @Value("${signal.state.event.assessment.lookBackPeriodGraceTimeSeconds}")
    public void setLookBackPeriodGraceTimeSeconds(long lookBackPeriodGraceTimeSeconds) {
        this.lookBackPeriodGraceTimeSeconds = lookBackPeriodGraceTimeSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignalStateEventAssessmentParameters)) {
            return false;
        }
        SignalStateEventAssessmentParameters signalStateVehicleCrossesParameters = (SignalStateEventAssessmentParameters) o;
        return debug == signalStateVehicleCrossesParameters.debug;
    }

    @Override
    public int hashCode() {
        return Objects.hash(debug);
    }

    @Override
    public String toString() {
        return "{" +
                ", debug='" + isDebug() + "'" +
                "}";
    }
}
