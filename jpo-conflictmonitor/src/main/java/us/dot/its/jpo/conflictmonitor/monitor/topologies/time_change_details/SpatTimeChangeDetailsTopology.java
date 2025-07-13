package us.dot.its.jpo.conflictmonitor.monitor.topologies.time_change_details;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.IntersectionReferenceAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.TimeChangeDetailsNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.TimeChangeDetailsNotificationAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.processors.SpatSequenceProcessorSupplier;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;

import java.util.ArrayList;
import java.util.List;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.TimeChangeDetailsConstants.DEFAULT_SPAT_TIME_CHANGE_DETAILS_ALGORITHM;
import static us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.RsuIntersectionSignalGroupKey;

@Component(DEFAULT_SPAT_TIME_CHANGE_DETAILS_ALGORITHM)
public class SpatTimeChangeDetailsTopology
        extends BaseStreamsTopology<SpatTimeChangeDetailsParameters>
        implements SpatTimeChangeDetailsStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SpatTimeChangeDetailsTopology.class);
    @Override
    protected Logger getLogger() {
        return logger;
    }

    private TimeChangeDetailsAggregationStreamsAlgorithm aggregationAlgorithm;

    @Override
    public Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(parameters.getSpatTimeChangeDetailsStateStoreName()),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.SpatTimeChangeDetailAggregator()
                )
        );

        var timeChangeEventStream = builder
                .stream(
                    parameters.getSpatInputTopicName(),
                    Consumed.with(
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat()
                    ))
                .process(new SpatSequenceProcessorSupplier(parameters),
                        parameters.getSpatTimeChangeDetailsStateStoreName());

        if (parameters.isAggregateEvents()) {
            // Aggregate events

            // New key includes all fields to aggregate on
            var timeChangeEventAggKeyStream = timeChangeEventStream.selectKey((key, value) -> {
                var aggKey = new TimeChangeDetailsAggregationKey();
                aggKey.setRsuId(key.getRsuId());
                aggKey.setRegion(key.getRegion());
                aggKey.setIntersectionId(key.getIntersectionId());
                aggKey.setSignalGroup(value.getSignalGroup());
                aggKey.setEventStateA(value.getFirstState());
                aggKey.setEventStateB(value.getSecondState());
                aggKey.setTimeMarkTypeA(value.getFirstTimeMarkType());
                aggKey.setTimeMarkTypeB(value.getSecondTimeMarkType());
                return aggKey;
            })
            // Use same partitioner, IntersectionIdPartitioner, so that repartition on new key will
            // not actually change the partitions of any items
            .repartition(
                    Repartitioned.with(JsonSerdes.TimeChangeDetailsAggregationKey(),
                            JsonSerdes.TimeChangeDetailsEvent())
                            .withStreamPartitioner(new IntersectionIdPartitioner<>()));

            KStream<TimeChangeDetailsAggregationKey, TimeChangeDetailsEventAggregation> timeChangeEventAggregationStream
                    = aggregationAlgorithm.buildTopology(builder, timeChangeEventAggKeyStream);

            // Notifications
            buildNotificationAggregationTopology(timeChangeEventAggregationStream);
        } else {
            // Don't aggregate events
            timeChangeEventStream.to(parameters.getSpatTimeChangeDetailsTopicName(),
                    Produced.with(
                            RsuIntersectionSignalGroupKey(),
                            JsonSerdes.TimeChangeDetailsEvent()
                    ));

            // Notifications
            buildNotificationTopology(timeChangeEventStream);
        }

        return builder.build();
    }

    // Notifications of non-aggregated events
    private void buildNotificationTopology(KStream<RsuIntersectionSignalGroupKey, TimeChangeDetailsEvent> timeChangeEventStream) {
        //timeChangeEventStream.print(Printed.toSysOut());

        KStream<String, TimeChangeDetailsNotification> timeChangeDetailsNotificationStream = timeChangeEventStream
                .flatMap(
                        (key, value) -> {
                            List<KeyValue<String, TimeChangeDetailsNotification>> result = new ArrayList<KeyValue<String, TimeChangeDetailsNotification>>();

                            TimeChangeDetailsNotification notification = new TimeChangeDetailsNotification();
                            notification.setEvent(value);
                            notification.setNotificationText(
                                    "Time Change Details Notification, generated because corresponding time change details event was generated.");
                            notification.setNotificationHeading("Time Change Details");
                            result.add(new KeyValue<>(notification.getKey(), notification));
                            return result;
                        });


        KTable<String, TimeChangeDetailsNotification> timeChangeDetailsNotificationTable = timeChangeDetailsNotificationStream
                .groupByKey(Grouped.with(Serdes.String(), JsonSerdes.TimeChangeDetailsNotification()))
                .reduce(
                        (oldValue, newValue) -> oldValue,
                        Materialized
                                .<String, TimeChangeDetailsNotification, KeyValueStore<Bytes, byte[]>>as(
                                        "TimeChangeDetailsNotification")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.TimeChangeDetailsNotification()));

        timeChangeDetailsNotificationTable.toStream().to(
                parameters.getSpatTimeChangeDetailsNotificationTopicName(),
                Produced.with(Serdes.String(),
                        JsonSerdes.TimeChangeDetailsNotification()));
    }

    // Notifications for aggregated events
    private void buildNotificationAggregationTopology(
            KStream<TimeChangeDetailsAggregationKey, TimeChangeDetailsEventAggregation> timeChangeEventAggregationStream) {

        timeChangeEventAggregationStream
                .mapValues(aggEvent -> {
                    var aggNotification = new TimeChangeDetailsNotificationAggregation();
                    aggNotification.setEventAggregation(aggEvent);
                    aggNotification.setNotificationText(
                            "Time Change Details Notification, " +
                                    "generated because one or more corresponding time change details events were generated.");
                    aggNotification.setNotificationHeading("Time Change Details");
                    return aggNotification;
                })
                .toTable(
                        Materialized.<TimeChangeDetailsAggregationKey,
                                        TimeChangeDetailsNotificationAggregation,
                                        KeyValueStore<Bytes, byte[]>>as(
                                                "IntersectionReferenceAlignmentNotificationAggregation")
                        .withKeySerde(JsonSerdes.TimeChangeDetailsAggregationKey())
                        .withValueSerde(JsonSerdes.TimeChangeDetailsNotificationAggregation()))
                .toStream()
                .to(parameters.getAggNotificationTopicName(),
                        Produced.with(
                            JsonSerdes.TimeChangeDetailsAggregationKey(),
                                JsonSerdes.TimeChangeDetailsNotificationAggregation(),
                                new IntersectionIdPartitioner<>()
                        ));
    }


    @Override
    public void setAggregationAlgorithm(TimeChangeDetailsAggregationAlgorithm aggregationAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (aggregationAlgorithm instanceof TimeChangeDetailsAggregationStreamsAlgorithm aggregationStreamsAlgorithm) {
            this.aggregationAlgorithm = aggregationStreamsAlgorithm;
        } else {
            throw new IllegalArgumentException("Aggregation algorithm must be a Streams algorithm");
        }
    }

    @Override
    protected void validate() {
        super.validate();
        if (aggregationAlgorithm == null) {
            throw new IllegalStateException("Aggregation algorithm is not set.");
        }
    }
}