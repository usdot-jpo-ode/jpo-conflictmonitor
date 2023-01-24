package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_reference_alignment_notification;



import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;

@Component
@PropertySource("classpath:intersectionReferenceAlignmentNotification-${intersection.reference.alignment.notification.properties}.properties")
public class IntersectionReferenceAlignmentNotificationParameters {



    // Whether to log diagnostic information for debugging
    boolean debug;
    String intersectionReferenceAlignmentEventTopicName;
    String intersectionReferenceAlignmentNotificationTopicName;
   
    public boolean isDebug() {
        return this.debug;
    }

    @Value("${intersection.reference.alignment.notification.debug}")
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getIntersectionReferenceAlignmentEventTopicName() {
        return intersectionReferenceAlignmentEventTopicName;
    }

    @Value("${intersection.reference.alignment.notification.intersectionReferenceAlignmentEventTopicName}")
    public void setIntersectionReferenceAlignmentEventTopicName(String intersectionReferenceAlignmentEventTopicName) {
        this.intersectionReferenceAlignmentEventTopicName = intersectionReferenceAlignmentEventTopicName;
    }

    public String getIntersectionReferenceAlignmentNotificationTopicName() {
        return intersectionReferenceAlignmentNotificationTopicName;
    }

    @Value("${intersection.reference.alignment.notification.intersectionReferenceAlignmentNotificationTopicName}")
    public void setIntersectionReferenceAlignmentNotificationTopicName(
            String intersectionReferenceAlignmentNotificationTopicName) {
        this.intersectionReferenceAlignmentNotificationTopicName = intersectionReferenceAlignmentNotificationTopicName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof IntersectionReferenceAlignmentNotificationParameters)) {
            return false;
        }
        IntersectionReferenceAlignmentNotificationParameters laneDirectionOfTravelParameters = (IntersectionReferenceAlignmentNotificationParameters) o;
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
