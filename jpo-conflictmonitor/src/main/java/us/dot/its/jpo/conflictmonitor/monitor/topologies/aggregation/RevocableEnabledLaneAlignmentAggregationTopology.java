package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_REVOCABLE_ENABLED_LANE_ALIGNMENT_AGGREGATION_ALGORITHM;

@Component(DEFAULT_REVOCABLE_ENABLED_LANE_ALIGNMENT_AGGREGATION_ALGORITHM)
@Slf4j
public class RevocableEnabledLaneAlignmentAggregationTopology
    extends
        BaseAggregationTopology<
                RevocableEnabledLaneAlignmentAggregationKey,
                RevocableEnabledLaneAlignmentEvent,
                RevocableEnabledLaneAlignmentEventAggregation>
    implements
        RevocableEnabledLaneAlignmentAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public RevocableEnabledLaneAlignmentEventAggregation constructEventAggregation(RevocableEnabledLaneAlignmentEvent event) {
        var aggEvent = new RevocableEnabledLaneAlignmentEventAggregation();
        aggEvent.setSource(event.getSource());
        aggEvent.setIntersectionID(event.getIntersectionID());
        aggEvent.setRoadRegulatorID(event.getRoadRegulatorID());
        // TODO fill in
        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new RevocableEnabledLaneAlignmentEventAggregation().getEventType();
    }

    @Override
    public Class<RevocableEnabledLaneAlignmentAggregationKey> keyClass() {
        return RevocableEnabledLaneAlignmentAggregationKey.class;
    }

    @Override
    public Serde<RevocableEnabledLaneAlignmentAggregationKey> keySerde() {
        return JsonSerdes.RevocableEnabledLaneAlignmentAggregationKey();
    }

    @Override
    public Serde<RevocableEnabledLaneAlignmentEvent> eventSerde() {
        return JsonSerdes.RevocableEnabledLaneAlignmentEvent();
    }

    @Override
    public Serde<RevocableEnabledLaneAlignmentEventAggregation> eventAggregationSerde() {
        return JsonSerdes.RevocableEnabledLaneAlignmentEventAggregation();
    }

    @Override
    public StreamPartitioner<RevocableEnabledLaneAlignmentAggregationKey, RevocableEnabledLaneAlignmentEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }
}
