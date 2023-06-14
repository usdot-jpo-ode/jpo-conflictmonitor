package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import java.io.IOException;

import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.ConnectionOfTravelNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.IntersectionReferenceAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.LaneDirectionOfTravelNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.SignalGroupAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.SignalStateConflictNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.TimeChangeDetailsNotification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationDeserializer implements Deserializer<Notification> {
    private static Logger logger = LoggerFactory.getLogger(Notification.class);

    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Notification deserialize(String topic, byte[] data) {
        if (data == null || data.length < 100) {
            return null;
        }
        try {
            JsonNode actualObj = mapper.readTree(data);
            Notification notification = deserializeNotification(actualObj);
            return notification;
        } catch (IOException e) {
            String errMsg = String.format("Exception deserializing for topic %s: %s", topic, e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }

    public Notification deserializeNotification(JsonNode actualObj) {
        try {
            // // // Deserialize the metadata
            String type = actualObj.get("notificationType").asText("");
            if (type.equals("ConnectionOfTravelNotification")) {
                return (Notification) mapper.treeToValue(actualObj, ConnectionOfTravelNotification.class);
            } else if (type.equals("IntersectionReferenceAlignmentNotification")) {
                return (Notification) mapper.treeToValue(actualObj, IntersectionReferenceAlignmentNotification.class);
            } else if (type.equals("LaneDirectionOfTravelAssessmentNotification")) {
                return (Notification) mapper.treeToValue(actualObj, LaneDirectionOfTravelNotification.class);
            } else if (type.equals("SignalGroupAlignmentNotification")) {
                return (Notification) mapper.treeToValue(actualObj, SignalGroupAlignmentNotification.class);
            } else if (type.equals("SignalStateConflictNotification")) {
                return (Notification) mapper.treeToValue(actualObj, SignalStateConflictNotification.class);
            } else if (type.equals("TimeChangeDetailsNotification")) {
                return (Notification) mapper.treeToValue(actualObj, TimeChangeDetailsNotification.class);
            } else {
                logger.info("Cannot Deserialize: " + type);
            }

        } catch (IOException e) {
            String errMsg = String.format("Exception deserializing=%s", e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
        return null;
    }
}
