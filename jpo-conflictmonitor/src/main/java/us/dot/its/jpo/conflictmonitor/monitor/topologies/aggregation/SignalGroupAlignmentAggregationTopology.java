package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalGroupAlignmentAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_SIGNAL_GROUP_ALIGNMENT_AGGREGATION_ALGORITHM;

@Component(DEFAULT_SIGNAL_GROUP_ALIGNMENT_AGGREGATION_ALGORITHM)
@Slf4j
public class SignalGroupAlignmentAggregationTopology
    extends
        BaseAggregationTopology<
                RsuIntersectionKey,
                SignalGroupAlignmentEvent,
                SignalGroupAlignmentEventAggregation>
    implements
        SignalGroupAlignmentAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public SignalGroupAlignmentEventAggregation constructEventAggregation(SignalGroupAlignmentEvent event) {
        var aggEvent = new SignalGroupAlignmentEventAggregation();
        aggEvent.setSource(event.getSource());
        aggEvent.setIntersectionID(event.getIntersectionID());
        aggEvent.setRoadRegulatorID(event.getRoadRegulatorID());
        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new SignalGroupAlignmentEventAggregation().getEventType();
    }

    @Override
    public Class<RsuIntersectionKey> keyClass() {
        return RsuIntersectionKey.class;
    }

    @Override
    public Serde<RsuIntersectionKey> keySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    public Serde<SignalGroupAlignmentEvent> eventSerde() {
        return JsonSerdes.SignalGroupAlignmentEvent();
    }

    @Override
    public Serde<SignalGroupAlignmentEventAggregation> eventAggregationSerde() {
        return JsonSerdes.SignalGroupAlignmentEventAggregation();
    }

    @Override
    public StreamPartitioner<RsuIntersectionKey, SignalGroupAlignmentEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }
}
