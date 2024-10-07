package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.app_health.KafkaStreamsAnomalyNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.MapTimestampDeltaNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.SpatTimestampDeltaNotification;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

/**
 * Base class Notification messages.
 * 
 * <p>Notifications are informational messages that signal
 * an anomaly or error condition.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "notificationType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConnectionOfTravelNotification.class, name = "ConnectionOfTravelNotification"),
        @JsonSubTypes.Type(value = IntersectionReferenceAlignmentNotification.class, name = "IntersectionReferenceAlignmentNotification"),
        @JsonSubTypes.Type(value = LaneDirectionOfTravelNotification.class, name = "LaneDirectionOfTravelAssessmentNotification"),
        @JsonSubTypes.Type(value = SignalGroupAlignmentNotification.class, name = "SignalGroupAlignmentNotification"),
        @JsonSubTypes.Type(value = SignalStateConflictNotification.class, name = "SignalStateConflictNotification"),
        @JsonSubTypes.Type(value = TimeChangeDetailsNotification.class, name = "TimeChangeDetailsNotification"),
        @JsonSubTypes.Type(value = KafkaStreamsAnomalyNotification.class, name = "AppHealthNotification"),
        @JsonSubTypes.Type(value = MapTimestampDeltaNotification.class, name = "MapTimestampDeltaNotification"),
        @JsonSubTypes.Type(value = SpatTimestampDeltaNotification.class, name = "SpatTimestampDeltaNotification"),
        @JsonSubTypes.Type(value = EventStateProgressionNotification.class, name = "EventStateProgressionNotification")
})
@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Notification {

    private static final Logger logger = LoggerFactory.getLogger(Notification.class);


    @Id
    public String id;
    public String key;
    public long notificationGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    public String notificationType;
    public String notificationText;
    public String notificationHeading;
    public int intersectionID;
    public int roadRegulatorID;

    public Notification() {
        this.notificationType = this.getClass().getSimpleName();
    }

    public Notification(String notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * When the notification expires.
     */
    public ZonedDateTime notificationExpiresAt;

    /**
     * @return A string that uniquely identifies the notification 
     * for purposes of suppressing duplicates within the Conflict Monitor.  
     * It should not depend on the absolute values of any timestamp fields.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public abstract String getUniqueId();

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
           logger.error("Exception serializing to json", e);
        }
        return testReturn;
    }
}
