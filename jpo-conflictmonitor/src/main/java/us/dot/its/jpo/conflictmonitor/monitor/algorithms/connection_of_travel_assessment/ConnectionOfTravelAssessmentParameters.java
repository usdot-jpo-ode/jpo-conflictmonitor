package us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment;


import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;


@Component
@PropertySource("classpath:connectionOfTravelAssessment-${connection.of.travel.assessment.properties}.properties")
public class ConnectionOfTravelAssessmentParameters {
    String connectionOfTravelEventTopicName;
    String connectionOfTravelAssessmentOutputTopicName;
    long lookBackPeriodDays;
    long lookBackPeriodGraceTimeSeconds;
    String connectionOfTravelNotificationTopicName;
    int minimumNumberOfEvents;


    // Whether to log diagnostic information for debugging
    boolean debug;

    public String getConnectionOfTravelEventTopicName() {
        return connectionOfTravelEventTopicName;
    }

    @Value("${connection.of.travel.assessment.connectionOfTravelEventTopicName}")
    public void setConnectionOfTravelEventTopicName(String connectionOfTravelEventTopicName) {
        this.connectionOfTravelEventTopicName = connectionOfTravelEventTopicName;
    }

    public String getConnectionOfTravelAssessmentOutputTopicName() {
        return connectionOfTravelAssessmentOutputTopicName;
    }

    @Value("${connection.of.travel.assessment.connectionOfTravelAssessmentOutputTopicName}")
    public void setConnectionOfTravelAssessmentOutputTopicName(String connectionOfTravelAssessmentOutputTopicName) {
        this.connectionOfTravelAssessmentOutputTopicName = connectionOfTravelAssessmentOutputTopicName;
    }

    public long getLookBackPeriodDays() {
        return lookBackPeriodDays;
    }

    @Value("${connection.of.travel.assessment.lookBackPeriodDays}")
    public void setLookBackPeriodDays(long lookBackPeriodDays) {
        this.lookBackPeriodDays = lookBackPeriodDays;
    }

    public long getLookBackPeriodGraceTimeSeconds() {
        return lookBackPeriodGraceTimeSeconds;
    }

    @Value("${connection.of.travel.assessment.lookBackPeriodGraceTimeSeconds}")
    public void setLookBackPeriodGraceTimeSeconds(long lookBackPeriodGraceTimeSeconds) {
        this.lookBackPeriodGraceTimeSeconds = lookBackPeriodGraceTimeSeconds;
    }

    public String getConnectionOfTravelNotificationTopicName() {
        return connectionOfTravelNotificationTopicName;
    }

    @Value("${connection.of.travel.assessment.connectionOfTravelNotificationTopicName=topic.CmConnectionOfTravelNotification}")
    public void setConnectionOfTravelNotificationTopicName(String connectionOfTravelNotificationTopicName) {
        this.connectionOfTravelNotificationTopicName = connectionOfTravelNotificationTopicName;
    }

    public int getMinimumNumberOfEvents() {
        return minimumNumberOfEvents;
    }

    @Value("${connection.of.travel.assessment.minimumNumberOfEvents}")
    public void setMinimumNumberOfEvents(int minimumNumberOfEvents) {
        this.minimumNumberOfEvents = minimumNumberOfEvents;
    }
   
    public boolean isDebug() {
        return this.debug;
    }

    @Value("${connection.of.travel.assessment.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ConnectionOfTravelAssessmentParameters)) {
            return false;
        }
        ConnectionOfTravelAssessmentParameters connectionOfTravelParameters = (ConnectionOfTravelAssessmentParameters) o;
        return debug == connectionOfTravelParameters.debug;
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
