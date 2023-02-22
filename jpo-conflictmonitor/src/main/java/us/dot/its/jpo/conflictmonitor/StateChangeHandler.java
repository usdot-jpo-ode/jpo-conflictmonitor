package us.dot.its.jpo.conflictmonitor;

import org.apache.commons.lang3.SystemUtils;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.app_health.KafkaStreamsStateChangeEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.app_health.KafkaStreamsAnomalyNotification;

/**
 * {@link StateListener} implementation that writes Kafka Streams state changes 
 * (eg. Running -> Error) to a topic.
 */
public class StateChangeHandler implements StateListener {

    final static Logger logger = LoggerFactory.getLogger(StateChangeHandler.class);

    final String topic;
    final String notificationTopic;
    final String topology;
    final KafkaTemplate<String, String> kafkaTemplate; 
    

    public StateChangeHandler(KafkaTemplate<String, String> kafkaTemplate, String topology, String topic, String notificationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topology = topology;
        this.topic = topic;
        this.notificationTopic = notificationTopic;
    }

    @Override
    public void onChange(State newState, State oldState) {
        try {
            var event = new KafkaStreamsStateChangeEvent();
            event.setAppId(SystemUtils.getHostName());
            event.setTopology(topology);
            event.setNewState(newState.toString());
            event.setOldState(oldState.toString());
            
            // Use topology name as key
            kafkaTemplate.send(topic, topology, event.toString());

            // Send a health notification if there is an error state
            if (State.ERROR.equals(newState)) {
                var notification = new KafkaStreamsAnomalyNotification();
                notification.setStateChange(event);
                notification.setNotificationHeading(String.format("Streams error on %s", topology));
                notification.setNotificationText(String.format("The streams topology '%s' state changed from '%s' to '%s', appId: '%s'", 
                    topology, event.getOldState(), event.getNewState(), event.getAppId()));
                // Unique id as key, send to compacted topic
                kafkaTemplate.send(notificationTopic, notification.getUniqueId(), notification.toString());
            }
        } catch (Exception ex) {
            logger.error("Exception sending kafka state change event", ex);
        }
    }
    
}
