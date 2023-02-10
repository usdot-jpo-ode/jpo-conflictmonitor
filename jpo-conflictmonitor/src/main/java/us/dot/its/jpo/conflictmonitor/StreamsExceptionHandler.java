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
 * Handler for unhandled exceptions thrown from Streams topologies that
 *  logs the exception to a topic, and allows choosing the shutdown behavior.
 * 
 * See {@link https://cwiki.apache.org/confluence/display/KAFKA/KIP-671%3A+Introduce+Kafka+Streams+Specific+Uncaught+Exception+Handler}
 * for a description of the options.
 */
public class StreamsExceptionHandler implements StreamsUncaughtExceptionHandler {

    final static Logger logger = LoggerFactory.getLogger(StateChangeHandler.class);

    final KafkaTemplate<String, String> kafkaTemplate;
    final String topology;
    final String notificationTopic;
    
    @Autowired
    public StreamsExceptionHandler(KafkaTemplate<String, String> kafkaTemplate, String topology, String notificationTopic) {
            this.kafkaTemplate = kafkaTemplate;
            this.topology = topology;
            this.notificationTopic = notificationTopic;
    }
    
    @Override
    public StreamThreadExceptionResponse handle(Throwable exception) {
        try {
            var event = new KafkaStreamsUnhandledExceptionEvent();
            event.setAppId(SystemUtils.getHostName());
            event.setTopology(topology);
            event.setException(exception);
            
            
            logger.error(String.format("Uncaught exception in stream topology %s", topology), exception);
            
            var notification = new KafkaStreamsAnomalyNotification();
            notification.setExceptionEvent(event);
            notification.setNotificationHeading(String.format("Streams error on %s", topology));
            notification.setNotificationText(event.getException().getMessage());
            // Unique id as key, send to compacted topic
            kafkaTemplate.send(notificationTopic, notification.getUniqueId(), notification.toString());
            
        } catch (Exception ex) {
            logger.error("Exception sending kafka streams error event", ex);
        }

        
        // SHUTDOWN_CLIENT option shuts down quickly.
        return StreamThreadExceptionResponse.SHUTDOWN_CLIENT;
        
        // SHUTDOWN_APPLICATION shuts down more slowly, but cleans up more thoroughly
        //return StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;

        // "Replace Thread" mode can be used to keep the streams client alive, 
        // however if the cause of the error was not transient, but due to a code error processing
        // a record, it can result in the record being repeatedly processed throwing the
        // same error
        //
        //return StreamThreadExceptionResponse.REPLACE_THREAD;
    }
    
}
