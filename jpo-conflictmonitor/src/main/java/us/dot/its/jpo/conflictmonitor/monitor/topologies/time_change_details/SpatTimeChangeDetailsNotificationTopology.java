//package us.dot.its.jpo.conflictmonitor.monitor.topologies.time_change_details;
//
//
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.common.utils.Bytes;
//import org.apache.kafka.streams.KeyValue;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.Topology;
//import org.apache.kafka.streams.kstream.*;
//import org.apache.kafka.streams.state.KeyValueStore;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
//import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
//import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsStreamsAlgorithm;
//import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
//import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.TimeChangeDetailsNotification;
//import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.TimeChangeDetailsConstants.DEFAULT_SPAT_TIME_CHANGE_DETAILS_NOTIFICATION_ALGORITHM;
//
//@Component(DEFAULT_SPAT_TIME_CHANGE_DETAILS_NOTIFICATION_ALGORITHM)
//public class SpatTimeChangeDetailsNotificationTopology
//        extends BaseStreamsTopology<SpatTimeChangeDetailsParameters>
//        implements SpatTimeChangeDetailsStreamsAlgorithm {
//
//    private static final Logger logger = LoggerFactory.getLogger(SpatTimeChangeDetailsTopology.class);
//
//
//
//    @Override
//    protected Logger getLogger() {
//        return logger;
//    }
//
//
//
//
//
//    public Topology buildTopology() {
//        var builder = new StreamsBuilder();
//
//        KStream<String, TimeChangeDetailsEvent> timeChangeDetailsStream = builder.stream(
//                parameters.getSpatTimeChangeDetailsTopicName(),
//                Consumed.with(
//                        Serdes.String(),
//                        JsonSerdes.TimeChangeDetailsEvent())
//        // .withTimestampExtractor(new SpatTimestampExtractor())
//        );
//
//        timeChangeDetailsStream.print(Printed.toSysOut());
//
//        KStream<String, TimeChangeDetailsNotification> timeChangeDetailsNotificationStream = timeChangeDetailsStream
//                .flatMap(
//                        (key, value) -> {
//                            List<KeyValue<String, TimeChangeDetailsNotification>> result = new ArrayList<KeyValue<String, TimeChangeDetailsNotification>>();
//
//                            TimeChangeDetailsNotification notification = new TimeChangeDetailsNotification();
//                            notification.setEvent(value);
//                            notification.setNotificationText(
//                                    "Time Change Details Notification, generated because corresponding time change details event was generated.");
//                            notification.setNotificationHeading("Time Change Details");
//                            result.add(new KeyValue<>(key, notification));
//                            return result;
//                        });
//
//        timeChangeDetailsNotificationStream.print(Printed.toSysOut());
//
//        KTable<String, TimeChangeDetailsNotification> timeChangeDetailsNotificationTable = timeChangeDetailsNotificationStream
//                .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.TimeChangeDetailsNotification()))
//                .reduce(
//                        (oldValue, newValue) -> {
//                            return oldValue;
//                        },
//                        Materialized
//                                .<String, TimeChangeDetailsNotification, KeyValueStore<Bytes, byte[]>>as(
//                                        "TimeChangeDetailsNotification")
//                                .withKeySerde(Serdes.String())
//                                .withValueSerde(JsonSerdes.TimeChangeDetailsNotification()));
//
//        timeChangeDetailsNotificationTable.toStream().to(
//                parameters.getSpatTimeChangeDetailsNotificationTopicName(),
//                Produced.with(Serdes.String(),
//                        JsonSerdes.TimeChangeDetailsNotification()));
//
//
//        return builder.build();
//    }
//
//
//
//
//}
//
//
//
