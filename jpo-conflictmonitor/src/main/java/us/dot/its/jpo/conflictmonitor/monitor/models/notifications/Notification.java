package us.dot.its.jpo.conflictmonitor.monitor.models.notifications;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

/**
 * Base class for Notification messages.
 * 
 * <p>Notifications are informational messages that signal
 * an anomaly or error condition.
 */
@Getter
@Setter
@EqualsAndHashCode
@Generated
public abstract class Notification {

    private static final Logger logger = LoggerFactory.getLogger(Notification.class);


    @Id
    public String id;
    public long notificationGeneratedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    public String notificationType;
    public String notificationText;
    public String notificationHeading;

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
