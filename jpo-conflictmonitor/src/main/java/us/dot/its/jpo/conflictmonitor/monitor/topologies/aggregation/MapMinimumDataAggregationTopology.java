package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.validation.map.MapMinimumDataAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_MAP_MINIMUM_DATA_AGGREGATION_ALGORITHM;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigConstants.CONFIG_TOPOLOGY;

@Component(DEFAULT_MAP_MINIMUM_DATA_AGGREGATION_ALGORITHM)
@Slf4j
public class MapMinimumDataAggregationTopology
    extends
        BaseAggregationTopology<
                RsuIntersectionKey,
                MapMinimumDataEvent,
                MapMinimumDataEventAggregation>
    implements
        MapMinimumDataAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public MapMinimumDataEventAggregation constructEventAggregation(MapMinimumDataEvent event) {
        var aggEvent = new MapMinimumDataEventAggregation();
        aggEvent.setSource(event.getSource());
        aggEvent.setIntersectionID(event.getIntersectionID());
        aggEvent.setRoadRegulatorID(event.getRoadRegulatorID());
        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new MapMinimumDataEventAggregation().getEventType();
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
    public Serde<MapMinimumDataEvent> eventSerde() {
        return JsonSerdes.MapMinimumDataEvent();
    }

    @Override
    public Serde<MapMinimumDataEventAggregation> eventAggregationSerde() {
        return JsonSerdes.MapMinimumDataEventAggregation();
    }

    @Override
    public StreamPartitioner<RsuIntersectionKey, MapMinimumDataEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }
}
