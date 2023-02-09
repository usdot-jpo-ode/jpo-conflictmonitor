package us.dot.its.jpo.conflictmonitor;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;


import us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health.KafkaStreamsUnhandledExceptionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.app_health.KafkaStreamsAnomalyNotification;



/**
 * Handler for unhandled exceptions thrown from Streams topologies
 * that always tries to restart streams and logs the exception to a topic.
 */
public class StreamsExceptionHandler implements StreamsUncaughtExceptionHandler {

    final static Logger logger = LoggerFactory.getLogger(StateChangeHandler.class);

    final KafkaTemplate<String, String> kafkaTemplate;
    final String topology;
    final String topic;
    final String notificationTopic;
    
    @Autowired
    public StreamsExceptionHandler(
        KafkaTemplate<String, String> kafkaTemplate,
        String topology, String topic, String notificationTopic) {
            this.kafkaTemplate = kafkaTemplate;
            this.topology = topology;
            this.topic = topic;
            this.notificationTopic = notificationTopic;
    }
    
    @Override
    public StreamThreadExceptionResponse handle(Throwable exception) {
        try {
            var event = new KafkaStreamsUnhandledExceptionEvent();
            event.setAppId(SystemUtils.getHostName());
            event.setTopology(topology);
            event.setException(exception);
            
            // Use topology name as key
            kafkaTemplate.send(topic, topology, event.toString());

            
            var notification = new KafkaStreamsAnomalyNotification();
            notification.setExceptionEvent(event);
            notification.setNotificationHeading(String.format("Streams error on %s", topology));
            notification.setNotificationText(ExceptionUtils.getStackTrace(event.getException()));
            // Unique id as key, send to compacted topic
            kafkaTemplate.send(notificationTopic, notification.getUniqueId(), notification.toString());
            
        } catch (Exception ex) {
            logger.error("Exception sending kafka streams error event", ex);
        }


        return StreamThreadExceptionResponse.REPLACE_THREAD;
    }
    
}
