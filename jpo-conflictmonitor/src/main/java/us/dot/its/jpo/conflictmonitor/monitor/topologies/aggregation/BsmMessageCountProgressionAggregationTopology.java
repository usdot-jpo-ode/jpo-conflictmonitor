package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_BSM_MESSAGE_COUNT_PROGRESSION_AGGREGATION_ALGORITHM;

@Component(DEFAULT_BSM_MESSAGE_COUNT_PROGRESSION_AGGREGATION_ALGORITHM)
@Slf4j
public class BsmMessageCountProgressionAggregationTopology
    extends
        BaseAggregationTopology<
                BsmMessageCountProgressionAggregationKey,
                BsmMessageCountProgressionEvent,
                BsmMessageCountProgressionEventAggregation>
    implements
        BsmMessageCountProgressionAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public BsmMessageCountProgressionEventAggregation constructEventAggregation(BsmMessageCountProgressionEvent event) {
        var aggEvent = new BsmMessageCountProgressionEventAggregation();
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
        return new BsmMessageCountProgressionEventAggregation().getEventType();
    }

    @Override
    public Class<BsmMessageCountProgressionAggregationKey> keyClass() {
        return BsmMessageCountProgressionAggregationKey.class;
    }

    @Override
    public Serde<BsmMessageCountProgressionAggregationKey> keySerde() {
        return JsonSerdes.BsmMessageCountProgressionAggregationKey();
    }

    @Override
    public Serde<BsmMessageCountProgressionEvent> eventSerde() {
        return JsonSerdes.BsmMessageCountProgressionEvent();
    }

    @Override
    public Serde<BsmMessageCountProgressionEventAggregation> eventAggregationSerde() {
        return JsonSerdes.BsmMessageCountProgressionEventAggregation();
    }

    @Override
    public StreamPartitioner<BsmMessageCountProgressionAggregationKey, BsmMessageCountProgressionEventAggregation> eventAggregationPartitioner() {
        return new RsuIdPartitioner<BsmMessageCountProgressionAggregationKey, BsmMessageCountProgressionEventAggregation>();
    }
}
