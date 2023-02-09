package us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_spat_message_assessment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "map.spat.message.assessment")
public class MapSpatMessageAssessmentParameters {
    // Whether to log diagnostic information for debugging
    boolean debug;

    String mapInputTopicName;
    String spatInputTopicName;
    String signalGroupAlignmentEventTopicName;
    String intersectionReferenceAlignmentEventTopicName;
    String signalStateConflictEventTopicName;
    String intersectionReferenceAlignmentNotificationTopicName;

}
