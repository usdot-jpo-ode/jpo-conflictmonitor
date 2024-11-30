package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_SPAT_MESSAGE_COUNT_PROGRESSION_AGGREGATION_ALGORITHM;

@Component(DEFAULT_SPAT_MESSAGE_COUNT_PROGRESSION_AGGREGATION_ALGORITHM)
@Slf4j
public class SpatMessageCountProgressionAggregationTopology
    extends
        BaseAggregationTopology<
                SpatMessageCountProgressionAggregationKey,
                SpatMessageCountProgressionEvent,
                SpatMessageCountProgressionEventAggregation>
    implements
        SpatMessageCountProgressionAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public SpatMessageCountProgressionEventAggregation constructEventAggregation(SpatMessageCountProgressionEvent event) {
        var aggEvent = new SpatMessageCountProgressionEventAggregation();
        aggEvent.setSource(event.getSource());
        aggEvent.setIntersectionID(event.getIntersectionID());
        aggEvent.setRoadRegulatorID(event.getRoadRegulatorID());
        aggEvent.setMessageType(event.getMessageType());

        // TODO
        //aggEvent.setDataFrame(event.getDataFrame());
        //aggEvent.setChange(event.getChange());

        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new SpatMessageCountProgressionEventAggregation().getEventType();
    }

    @Override
    public Class<SpatMessageCountProgressionAggregationKey> keyClass() {
        return SpatMessageCountProgressionAggregationKey.class;
    }

    @Override
    public Serde<SpatMessageCountProgressionAggregationKey> keySerde() {
        return JsonSerdes.SpatMessageCountProgressionAggregationKey();
    }

    @Override
    public Serde<SpatMessageCountProgressionEvent> eventSerde() {
        return JsonSerdes.SpatMessageCountProgressionEvent();
    }

    @Override
    public Serde<SpatMessageCountProgressionEventAggregation> eventAggregationSerde() {
        return JsonSerdes.SpatMessageCountProgressionEventAggregation();
    }

    @Override
    public StreamPartitioner<SpatMessageCountProgressionAggregationKey, SpatMessageCountProgressionEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }
}
