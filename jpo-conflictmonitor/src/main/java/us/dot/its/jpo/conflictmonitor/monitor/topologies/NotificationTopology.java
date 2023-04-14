package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationConstants.DEFAULT_NOTIFICATION_ALGORITHM;


@Component(DEFAULT_NOTIFICATION_ALGORITHM)
public class NotificationTopology
        extends BaseStreamsTopology<NotificationParameters>
        implements NotificationStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTopology.class);



    @Override
    protected Logger getLogger() {
        return logger;
    }



    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Notification> cotStream = builder.stream(parameters.getConnectionOfTravelNotificationTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Notification()));
        KStream<String, Notification> allNotifications = cotStream
            .merge(builder.stream(parameters.getLaneDirectionOfTravelNotificationTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Notification())))
            .merge(builder.stream(parameters.getIntersectionReferenceAlignmentNotificationTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Notification())))
            .merge(builder.stream(parameters.getSignalGroupAlignmentNotificationTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Notification())))
            .merge(builder.stream(parameters.getSignalStateConflictNotificationTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Notification())))
            .merge(builder.stream(parameters.getSpatTimeChangeDetailsNotificationTopicName(), Consumed.with(Serdes.String(), JsonSerdes.Notification())));        

        allNotifications.to(parameters.getNotificationOutputTopicName(), Produced.with(Serdes.String(), JsonSerdes.Notification()));
        if(parameters.isDebug()){
            allNotifications.print(Printed.toSysOut());
        }
        


        return builder.build();

    }



}
