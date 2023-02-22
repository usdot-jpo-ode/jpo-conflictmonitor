package us.dot.its.jpo.conflictmonitor.monitor.topologies.time_change_details;



import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.TimeChangeDetailsConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.StreamsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.TimeChangeDetailsNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimeChangeDetailAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.processors.SpatSequenceProcessorSupplier;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

@Component(DEFAULT_SPAT_TIME_CHANGE_DETAILS_NOTIFICATION_ALGORITHM)
public class SpatTimeChangeDetailsNotificationTopology implements SpatTimeChangeDetailsStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SpatTimeChangeDetailsTopology.class);

    SpatTimeChangeDetailsParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;

    @Override
    public void setParameters(SpatTimeChangeDetailsParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public SpatTimeChangeDetailsParameters getParameters() {
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
        logger.info("Starting SpatTimeChangeDetailsTopology.");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
        logger.info("Started SpatTimeChangeDetailsTopology.");


        //Topology topology = BsmEventTopology.build(conflictMonitorProps.getKafkaTopicOdeBsmJson(), conflictMonitorProps.getKafkaTopicCmBsmEvent());
        // KafkaStreams streams = new KafkaStreams(topology, conflictMonitorProps.createStreamProperties("bsmEvent"));
        // Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        // streams.start(); 
    }

    public Topology buildTopology() {
        var builder = new StreamsBuilder();

        KStream<String, TimeChangeDetailsEvent> timeChangeDetailsStream = builder.stream(
                parameters.getSpatTimeChangeDetailsTopicName(),
                Consumed.with(
                        Serdes.String(),
                        JsonSerdes.TimeChangeDetailsEvent())
        // .withTimestampExtractor(new SpatTimestampExtractor())
        );

        timeChangeDetailsStream.print(Printed.toSysOut());

        KStream<String, TimeChangeDetailsNotification> timeChangeDetailsNotificationStream = timeChangeDetailsStream
                .flatMap(
                        (key, value) -> {
                            List<KeyValue<String, TimeChangeDetailsNotification>> result = new ArrayList<KeyValue<String, TimeChangeDetailsNotification>>();

                            TimeChangeDetailsNotification notification = new TimeChangeDetailsNotification();
                            notification.setEvent(value);
                            notification.setNotificationText(
                                    "Time Change Details Notification, generated because corresponding time change details event was generated.");
                            notification.setNotificationHeading("Time Change Details");
                            result.add(new KeyValue<>(key, notification));
                            return result;
                        });

        timeChangeDetailsNotificationStream.print(Printed.toSysOut());

        KTable<String, TimeChangeDetailsNotification> timeChangeDetailsNotificationTable = timeChangeDetailsNotificationStream
                .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.TimeChangeDetailsNotification()))
                .reduce(
                        (oldValue, newValue) -> {
                            return oldValue;
                        },
                        Materialized
                                .<String, TimeChangeDetailsNotification, KeyValueStore<Bytes, byte[]>>as(
                                        "TimeChangeDetailsNotification")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.TimeChangeDetailsNotification()));

        timeChangeDetailsNotificationTable.toStream().to(
                parameters.getSpatTimeChangeDetailsNotificationTopicName(),
                Produced.with(Serdes.String(),
                        JsonSerdes.TimeChangeDetailsNotification()));
        
        
        return builder.build();
    }



    

    @Override
    public void stop() {
        logger.info("Stopping SpatBroadcastRateTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped SpatBroadcastRateTopology.");
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



