package us.dot.its.jpo.conflictmonitor.monitor.topologies;



import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.LaneTypeAttributesMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.RevocableEnabledLaneAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.RevocableEnabledLaneAlignmentNotificationAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.SignalGroupAlignmentNotificationAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.utils.SpatUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import us.dot.its.jpo.ode.plugin.j2735.J2735LaneTypeAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentConstants.DEFAULT_REVOCABLE_ENABLED_LANE_ALIGNMENT_ALGORITHM;
import static us.dot.its.jpo.conflictmonitor.monitor.utils.ProcessedMapUtils.*;

/**
 * Revocable/Enabled Lane Alignment Algorithm implementation.
 * This is a subtopology of {@link MapSpatMessageAssessmentTopology}.
 */
@Component(DEFAULT_REVOCABLE_ENABLED_LANE_ALIGNMENT_ALGORITHM)
@Slf4j
public class RevocableEnabledLaneAlignmentTopology
    extends BaseStreamsBuilder<RevocableEnabledLaneAlignmentParameters>
    implements RevocableEnabledLaneAlignmentStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm aggregationAlgorithm;

    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, SpatMap> spatMapStream) {

        KStream<RsuIntersectionKey, RevocableEnabledLaneAlignmentEvent> eventStream =
            spatMapStream.map((rsuIntersectionKey, spatMap) -> {
                var candidateEvent = new RevocableEnabledLaneAlignmentEvent();
                candidateEvent.setIntersectionID(rsuIntersectionKey.getIntersectionId());
                candidateEvent.setRoadRegulatorID(rsuIntersectionKey.getRegion());
                candidateEvent.setTimestamp(SpatUtils.getTimestamp(spatMap.getSpat()));
                candidateEvent.setSource(spatMap.getSpat().getOriginIp());

                // Check the MAP for revocable lanes
                ProcessedMap<LineString> map = spatMap.getMap();

                LaneTypeAttributesMap allLaneAttributes
                        = getLaneTypeAttributesMap(map);

                candidateEvent.setLaneTypeAttributes(allLaneAttributes);

                Set<Integer> revocableLanes
                        = allLaneAttributes.entrySet().stream()
                        .filter(entry -> entry.getValue() != null
                                && entry.getValue().revocable())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toUnmodifiableSet());

                candidateEvent.setRevocableLaneList(revocableLanes);

                // Check the SPAT for enabled lanes
                ProcessedSpat spat = spatMap.getSpat();
                List<Integer> enabledLanes = spat.getEnabledLanes();
                if (enabledLanes != null) {
                    candidateEvent.setEnabledLaneList(Set.copyOf(spat.getEnabledLanes()));
                } else {
                    candidateEvent.setEnabledLaneList(Set.of());
                }

                if (this.parameters.isDebug()) {
                    log.info("MAP revocable lanes: {}", candidateEvent.getRevocableLaneList());
                    log.info("SPAT enabled lanes: {}", candidateEvent.getEnabledLaneList());
                }

                return new KeyValue<>(rsuIntersectionKey, candidateEvent);
            }).filter((rsuIntersectionKey, candidateEvent) -> {
                Set<Integer> revocableLanes = candidateEvent.getRevocableLaneList();
                Set<Integer> enabledLanes = candidateEvent.getEnabledLaneList();

                // No revocable lanes or enabled lanes
                if (revocableLanes.isEmpty() && enabledLanes.isEmpty()) {
                    return false;
                }

                Set<Integer> allLanes = candidateEvent.getLaneTypeAttributes().keySet();
                if (!allLanes.containsAll(enabledLanes)) {
                    // Enabled lane doesn't match any lane in the MAP: generate event
                    return true;
                }

                if (!revocableLanes.containsAll(enabledLanes)) {
                    // Enabled lanes has lanes that aren't in the revocable list: generate event
                    return true;
                }

                return false;

            });

        if (parameters.isAggregateEvents()) {
            // Aggregate events
            KStream<RevocableEnabledLaneAlignmentAggregationKey, RevocableEnabledLaneAlignmentEvent> aggKeyStream
                    = eventStream.selectKey((key, value) -> {
                        var newKey =  new RevocableEnabledLaneAlignmentAggregationKey();
                        newKey.setRsuId(key.getRsuId());
                        newKey.setIntersectionId(key.getIntersectionId());
                        newKey.setRevocableLaneList(value.getRevocableLaneList());
                        newKey.setEnabledLaneList(value.getEnabledLaneList());
                return newKey;
            });
            KStream<RevocableEnabledLaneAlignmentAggregationKey, RevocableEnabledLaneAlignmentEventAggregation> eventAggregationStream =
                aggregationAlgorithm.buildTopology(builder, aggKeyStream);

            // Notifications
            buildNotificationAggregationTopology(eventAggregationStream);
        } else {
            // Don't aggregate events: send each
            eventStream.to(parameters.getOutputTopicName(),
                    Produced.with(
                            us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                            JsonSerdes.RevocableEnabledLaneAlignmentEvent()));

            // Notifications
            buildNotificationTopology(eventStream);
        }
    }

    @Override
    public void setAggregationAlgorithm(RevocableEnabledLaneAlignmentAggregationAlgorithm aggregationAlgorithm) {
        // Enforce the algorithm being a Streams algorithm
        if (aggregationAlgorithm instanceof RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm) {
            this.aggregationAlgorithm = (RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm) aggregationAlgorithm;
        } else {
            throw new IllegalArgumentException("Aggregation algorithm must be a streams algorithm");
        }
    }

    private void buildNotificationTopology(KStream<RsuIntersectionKey, RevocableEnabledLaneAlignmentEvent> eventStream) {
        eventStream
                .mapValues(event -> {
                    var notification = new RevocableEnabledLaneAlignmentNotification();
                    notification.setEvent(event);
                    notification.setNotificationText("Revocable Lane Alignment Notification, " +
                            "Generated because of a mismatch between MAP revocable lanes and SPAT enabled lanes.");
                    return notification;
                })
                .toTable(Materialized.<
                        RsuIntersectionKey,
                        RevocableEnabledLaneAlignmentNotification,
                        KeyValueStore<Bytes, byte[]>>as("RevocableEnabledLaneAlignmentNotificationAggregation")
                            .withKeySerde(us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey())
                            .withValueSerde(JsonSerdes.RevocableEnabledLaneAlignmentNotification()))
                .toStream()
                .to(parameters.getOutputTopicName(), Produced.with(
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.RevocableEnabledLaneAlignmentNotification()
                ));

    }

    private void buildNotificationAggregationTopology(
            KStream<RevocableEnabledLaneAlignmentAggregationKey, RevocableEnabledLaneAlignmentEventAggregation> eventAggregationStream
    ) {
        eventAggregationStream
                .mapValues(aggEvent -> {
                    var aggNotification = new RevocableEnabledLaneAlignmentNotificationAggregation();
                    aggNotification.setEventAggregation(aggEvent);
                    aggNotification.setNotificationText("MAP-SPAT Revocable/Enabled Lane Alignment Notification Aggregation, " +
                            "Generated because of a mismatch between MAP revocable lanes and SPAT enabled lanes.");
                    aggNotification.setNotificationHeading("MAP-SPAT Revocable/Enabled Lane Alignment Aggregation");
                    return aggNotification;
                })
                .toTable(
                        Materialized.<RevocableEnabledLaneAlignmentAggregationKey,
                                        RevocableEnabledLaneAlignmentNotificationAggregation,
                                        KeyValueStore<Bytes, byte[]>>as("RevocableEnabledLaneAlignmentNotificationAggregation")
                                .withKeySerde(JsonSerdes.RevocableEnabledLaneAlignmentAggregationKey())
                                .withValueSerde(JsonSerdes.RevocableEnabledLaneAlignmentNotificationAggregation())
                )
                .toStream()
                .to(parameters.getAggNotificationTopicName(),
                        Produced.with(
                                JsonSerdes.RevocableEnabledLaneAlignmentAggregationKey(),
                                JsonSerdes.RevocableEnabledLaneAlignmentNotificationAggregation(),
                                new IntersectionIdPartitioner<>()
                        ));

    }
}
