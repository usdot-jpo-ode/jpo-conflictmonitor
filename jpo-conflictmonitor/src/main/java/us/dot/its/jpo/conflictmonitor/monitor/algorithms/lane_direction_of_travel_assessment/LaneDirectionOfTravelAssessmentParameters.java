package us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment;


import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;

@Component
@PropertySource("classpath:laneDirectionOfTravelAssessment-${lane.direction.of.travel.assessment.properties}.properties")
public class LaneDirectionOfTravelAssessmentParameters {


    // Whether to log diagnostic information for debugging
    boolean debug;
    String laneDirectionOfTravelEventTopicName;
    String laneDirectionOfTravelAssessmentOutputTopicName;
    String laneDirectionOfTravelNotificationOutputTopicName;
    long lookBackPeriodDays;
    long lookBackPeriodGraceTimeSeconds;
    double headingToleranceDegrees; 
    int minimumNumberOfEvents;   

    

    public String getLaneDirectionOfTravelEventTopicName() {
        return laneDirectionOfTravelEventTopicName;
    }

    @Value("${lane.direction.of.travel.assessment.laneDirectionOfTravelEventTopicName}")
    public void setLaneDirectionOfTravelEventTopicName(String laneDirectionOfTravelEventTopicName) {
        this.laneDirectionOfTravelEventTopicName = laneDirectionOfTravelEventTopicName;
    }

    public String getLaneDirectionOfTravelAssessmentOutputTopicName() {
        return laneDirectionOfTravelAssessmentOutputTopicName;
    }

    @Value("${lane.direction.of.travel.assessment.laneDirectionOfTravelAssessmentOutputTopicName}")
    public void setLaneDirectionOfTravelAssessmentOutputTopicName(String laneDirectionOfTravelAssessmentOutputTopicName) {
        this.laneDirectionOfTravelAssessmentOutputTopicName = laneDirectionOfTravelAssessmentOutputTopicName;
    }

    public long getLookBackPeriodDays() {
        return lookBackPeriodDays;
    }

    @Value("${lane.direction.of.travel.assessment.lookBackPeriodDays}")
    public void setLookBackPeriodDays(long lookBackPeriodDays) {
        this.lookBackPeriodDays = lookBackPeriodDays;
    }

    public long getLookBackPeriodGraceTimeSeconds() {
        return lookBackPeriodGraceTimeSeconds;
    }

    @Value("${lane.direction.of.travel.assessment.lookBackPeriodGraceTimeSeconds}")
    public void setLookBackPeriodGraceTimeSeconds(long lookBackPeriodGraceTimeSeconds) {
        this.lookBackPeriodGraceTimeSeconds = lookBackPeriodGraceTimeSeconds;
    }

    public double getHeadingToleranceDegrees() {
        return headingToleranceDegrees;
    }

    @Value("${lane.direction.of.travel.assessment.headingToleranceDegrees}")
    public void setHeadingToleranceDegrees(double headingToleranceDegrees) {
        this.headingToleranceDegrees = headingToleranceDegrees;
    }

    public int getMinimumNumberOfEvents() {
        return minimumNumberOfEvents;
    }

    @Value("${lane.direction.of.travel.assessment.minimumNumberOfEvents}")
    public void setMinimumNumberOfEvents(int minimumNumberOfEvents) {
        this.minimumNumberOfEvents = minimumNumberOfEvents;
    }

    public String getLaneDirectionOfTravelNotificationOutputTopicName() {
        return laneDirectionOfTravelNotificationOutputTopicName;
    }

    @Value("${lane.direction.of.travel.assessment.laneDirectionOfTravelAssessmentNotificationOutputTopicName}")
    public void setLaneDirectionOfTravelNotificationOutputTopicName(
            String laneDirectionOfTravelNotificationOutputTopicName) {
        this.laneDirectionOfTravelNotificationOutputTopicName = laneDirectionOfTravelNotificationOutputTopicName;
    }


   
    public boolean isDebug() {
        return this.debug;
    }

    @Value("${lane.direction.of.travel.assessment.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LaneDirectionOfTravelAssessmentParameters)) {
            return false;
        }
        LaneDirectionOfTravelAssessmentParameters laneDirectionOfTravelParameters = (LaneDirectionOfTravelAssessmentParameters) o;
        return debug == laneDirectionOfTravelParameters.debug;
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
