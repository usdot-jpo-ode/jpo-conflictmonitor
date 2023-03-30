package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationConstants.*;

import java.util.Properties;


@Component(DEFAULT_NOTIFICATION_ALGORITHM)
public class NotificationTopology implements NotificationStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTopology.class);

    NotificationParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(NotificationParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public NotificationParameters getParameters() {
        return parameters;
    }

    @Override
    public void setStreamsProperties(Properties streamsProperties) {
       this.streamsProperties = streamsProperties;
    }

    @Override
    public Properties getStreamsProperties() {
        return streamsProperties;
    }

    @Override
    public KafkaStreams getStreams() {
        return streams;
    }

    @Override
    public void start() {
        if (parameters == null) {
            throw new IllegalStateException("Start called before setting parameters.");
        }
        if (streamsProperties == null) {
            throw new IllegalStateException("Streams properties are not set.");
        }
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        logger.info("Starting Notification Topology.");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        streams.start();
        logger.info("Started Notification Topology");
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

    @Override
    public void stop() {
        logger.info("Stopping BSMNotificationTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped BSMNotificationTopology.");
    }

    
    StateListener stateListener;

    @Override
    public void registerStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    StreamsUncaughtExceptionHandler exceptionHandler;

    @Override
    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
