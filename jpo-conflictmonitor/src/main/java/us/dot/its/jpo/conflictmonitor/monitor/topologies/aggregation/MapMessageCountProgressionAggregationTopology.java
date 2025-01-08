package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_MAP_MESSAGE_COUNT_PROGRESSION_AGGREGATION_ALGORITHM;

@Component(DEFAULT_MAP_MESSAGE_COUNT_PROGRESSION_AGGREGATION_ALGORITHM)
@Slf4j
public class MapMessageCountProgressionAggregationTopology
    extends
        BaseAggregationTopology<
                MapMessageCountProgressionAggregationKey,
                MapMessageCountProgressionEvent,
                MapMessageCountProgressionEventAggregation>
    implements
        MapMessageCountProgressionAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public MapMessageCountProgressionEventAggregation constructEventAggregation(MapMessageCountProgressionEvent event) {
        var aggEvent = new MapMessageCountProgressionEventAggregation();
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
        return new MapMessageCountProgressionEventAggregation().getEventType();
    }

    @Override
    public Class<MapMessageCountProgressionAggregationKey> keyClass() {
        return MapMessageCountProgressionAggregationKey.class;
    }

    @Override
    public Serde<MapMessageCountProgressionAggregationKey> keySerde() {
        return JsonSerdes.MapMessageCountProgressionAggregationKey();
    }

    @Override
    public Serde<MapMessageCountProgressionEvent> eventSerde() {
        return JsonSerdes.MapMessageCountProgressionEvent();
    }

    @Override
    public Serde<MapMessageCountProgressionEventAggregation> eventAggregationSerde() {
        return JsonSerdes.MapMessageCountProgressionEventAggregation();
    }

    @Override
    public StreamPartitioner<MapMessageCountProgressionAggregationKey, MapMessageCountProgressionEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }
}
