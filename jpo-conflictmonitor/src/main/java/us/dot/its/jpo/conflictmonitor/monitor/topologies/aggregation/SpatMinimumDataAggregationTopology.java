package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.SpatMinimumDataAggregationAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.SpatMinimumDataAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation.EventAggregationProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_SPAT_MINIMUM_DATA_AGGREGATION_ALGORITHM;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigConstants.CONFIG_TOPOLOGY;

@Component(DEFAULT_SPAT_MINIMUM_DATA_AGGREGATION_ALGORITHM)
@Slf4j
@DependsOn({CONFIG_TOPOLOGY})
public class SpatMinimumDataAggregationTopology
        extends
            BaseAggregationTopology<
                    RsuIntersectionKey,
                    SpatMinimumDataEvent,
                    SpatMinimumDataEventAggregation>
        implements
            SpatMinimumDataAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
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
    public Serde<SpatMinimumDataEvent> eventSerde() {
        return JsonSerdes.SpatMinimumDataEvent();
    }

    @Override
    public Serde<SpatMinimumDataEventAggregation> eventAggregationSerde() {
        return JsonSerdes.SpatMinimumDataEventAggregation();
    }

    @Override
    public StreamPartitioner<RsuIntersectionKey, SpatMinimumDataEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }

    @Override
    public SpatMinimumDataEventAggregation constructEventAggregation(SpatMinimumDataEvent event) {
        var aggEvent = new SpatMinimumDataEventAggregation();
        aggEvent.setSource(event.getSource());
        aggEvent.setIntersectionID(event.getIntersectionID());
        aggEvent.setRoadRegulatorID(event.getRoadRegulatorID());
        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new SpatMinimumDataEventAggregation().getEventType();
    }

}
