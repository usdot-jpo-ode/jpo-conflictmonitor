package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.time_change_details.TimeChangeDetailsAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_TIME_CHANGE_DETAILS_AGGREGATION_ALGORITHM;

@Component(DEFAULT_TIME_CHANGE_DETAILS_AGGREGATION_ALGORITHM)
@Slf4j
public class TimeChangeDetailsAggregationTopology
    extends
        BaseAggregationTopology<
                TimeChangeDetailsAggregationKey,
                TimeChangeDetailsEvent,
                TimeChangeDetailsEventAggregation>
    implements
        TimeChangeDetailsAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public TimeChangeDetailsEventAggregation constructEventAggregation(TimeChangeDetailsEvent event) {
        var aggEvent = new TimeChangeDetailsEventAggregation();
        aggEvent.setSource(event.getSource());
        aggEvent.setIntersectionID(event.getIntersectionID());
        aggEvent.setRoadRegulatorID(event.getRoadRegulatorID());
        aggEvent.setTimeMarkTypeA(event.getFirstTimeMarkType());
        aggEvent.setTimeMarkTypeB(event.getSecondTimeMarkType());
        aggEvent.setEventStateA(event.getFirstState());
        aggEvent.setEventStateB(event.getSecondState());
        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new TimeChangeDetailsEventAggregation().getEventType();
    }

    @Override
    public Class<TimeChangeDetailsAggregationKey> keyClass() {
        return TimeChangeDetailsAggregationKey.class;
    }

    @Override
    public Serde<TimeChangeDetailsAggregationKey> keySerde() {
        return JsonSerdes.TimeChangeDetailsAggregationKey();
    }

    @Override
    public Serde<TimeChangeDetailsEvent> eventSerde() {
        return JsonSerdes.TimeChangeDetailsEvent();
    }

    @Override
    public Serde<TimeChangeDetailsEventAggregation> eventAggregationSerde() {
        return JsonSerdes.TimeChangeDetailsEventAggregation();
    }

    @Override
    public StreamPartitioner<TimeChangeDetailsAggregationKey, TimeChangeDetailsEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }
}
