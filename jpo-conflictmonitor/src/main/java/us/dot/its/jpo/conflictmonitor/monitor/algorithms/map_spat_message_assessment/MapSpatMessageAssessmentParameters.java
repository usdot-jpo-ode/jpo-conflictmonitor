package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:mapSpatMessageAssessment-${map.spat.message.assessment.properties}.properties")
public class MapSpatMessageAssessmentParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;

    String mapInputTopicName;
    String spatInputTopicName;
    String signalGroupAlignmentEventTopicName;
    String intersectionReferenceAlignmentEventTopicName;
    String signalStateConflictEventTopicName;
    String intersectionReferenceAlignmentNotificationTopicName;

    

    

    public boolean isDebug() {
        return this.debug;
    }

    @Value("${map.spat.message.assessment.debug}")
     public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getMapInputTopicName() {
        return mapInputTopicName;
    }

    @Value("${map.spat.message.assessment.mapInputTopicName}")
    public void setMapInputTopicName(String mapInputTopicName) {
        this.mapInputTopicName = mapInputTopicName;
    }

    public String getSpatInputTopicName() {
        return spatInputTopicName;
    }

    @Value("${map.spat.message.assessment.spatInputTopicName}")
    public void setSpatInputTopicName(String spatInputTopicName) {
        this.spatInputTopicName = spatInputTopicName;
    }

    public String getSignalGroupAlignmentEventTopicName() {
        return signalGroupAlignmentEventTopicName;
    }

    @Value("${map.spat.message.assessment.signalGroupAlignmentEventTopicName}")
    public void setSignalGroupAlignmentEventTopicName(String signalGroupAlignmentEventTopicName) {
        this.signalGroupAlignmentEventTopicName = signalGroupAlignmentEventTopicName;
    }

    public String getIntersectionReferenceAlignmentEventTopicName() {
        return intersectionReferenceAlignmentEventTopicName;
    }

    @Value("${map.spat.message.assessment.intersectionReferenceAlignmentEventTopicName}")
    public void setIntersectionReferenceAlignmentEventTopicName(String intersectionReferenceAlignmentEventTopicName) {
        this.intersectionReferenceAlignmentEventTopicName = intersectionReferenceAlignmentEventTopicName;
    }

    public String getSignalStateConflictEventTopicName() {
        return signalStateConflictEventTopicName;
    }

    @Value("${map.spat.message.assessment.signalStateConflictEventTopicName}")
    public void setSignalStateConflictEventTopicName(String signalStateConflictEventTopicName) {
        this.signalStateConflictEventTopicName = signalStateConflictEventTopicName;
    }

    public String getIntersectionReferenceAlignmentNotificationTopicName() {
        return intersectionReferenceAlignmentNotificationTopicName;
    }

    @Value("${map.spat.message.assessment.intersectionReferenceAlignmentNotificationTopicName}")
    public void setIntersectionReferenceAlignmentNotificationTopicName(
            String intersectionReferenceAlignmentNotificationTopicName) {
        this.intersectionReferenceAlignmentNotificationTopicName = intersectionReferenceAlignmentNotificationTopicName;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MapSpatMessageAssessmentParameters)) {
            return false;
        }
        MapSpatMessageAssessmentParameters signalStateVehicleCrossesParameters = (MapSpatMessageAssessmentParameters) o;
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
