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
 * Base class for notification messages.
 * <p>
 * Notifications are informational messages that signal
 * an anomaly or error condition within the Conflict Monitor system.
 * <p>
 * This class is intended to be extended by specific notification types.
 * It provides common fields and serialization configuration for all notifications.
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
        @JsonSubTypes.Type(value = EventStateProgressionNotification.class, name = "EventStateProgressionNotification"),
        @JsonSubTypes.Type(value = EventStateProgressionNotificationAggregation.class, name = "EventStateProgressionNotificationAggregation"),
        @JsonSubTypes.Type(value = IntersectionReferenceAlignmentNotificationAggregation.class, name = "IntersectionReferenceAlignmentNotificationAggregation"),
        @JsonSubTypes.Type(value = SignalGroupAlignmentNotificationAggregation.class, name = "SignalGroupAlignmentNotificationAggregation"),
        @JsonSubTypes.Type(value = SignalStateConflictNotificationAggregation.class, name = "SignalStateConflictNotificationAggregation"),
        @JsonSubTypes.Type(value = TimeChangeDetailsNotificationAggregation.class, name = "TimeChangeDetailsNotificationAggregation"),
        @JsonSubTypes.Type(value = RevocableEnabledLaneAlignmentNotification.class, name = "RevocableEnabledLaneAlignmentNotification"),
        @JsonSubTypes.Type(value = RevocableEnabledLaneAlignmentNotificationAggregation.class, name = "RevocableEnabledLaneAlignmentNotificationAggregation")
})
@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Notification {

    /** Logger for Notification operations. */
    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    /** Unique identifier for the notification (used by persistence frameworks). */
    @Id
    public String id;

    /** Key used for deduplication or lookup of notifications. */
    public String key;

    /** Epoch milliseconds when the notification was generated. */
    public long notificationGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();

    /** The type of notification (used for serialization and deserialization). */
    public String notificationType;

    /** Textual description of the notification. */
    public String notificationText;

    /** Heading or title for the notification. */
    public String notificationHeading;

    /** Intersection ID associated with the notification. */
    public int intersectionID;

    /** Road regulator ID associated with the notification. */
    public int roadRegulatorID;

    /**
     * Default constructor.
     * Sets the notificationType to the simple class name.
     */
    public Notification() {
        this.notificationType = this.getClass().getSimpleName();
    }

    /**
     * Constructs a Notification with a specified notification type.
     * 
     * @param notificationType the type of notification
     */
    public Notification(String notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * When the notification expires.
     */
    public ZonedDateTime notificationExpiresAt;

    /**
     * Returns a string that uniquely identifies the notification for purposes of suppressing duplicates
     * within the Conflict Monitor. It should not depend on the absolute values of any timestamp fields.
     *
     * @return a unique identifier string for this notification
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public abstract String getUniqueId();

    /**
     * Serializes the notification to a JSON string.
     *
     * @return JSON string representation of the notification
     */
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
