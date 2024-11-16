package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_EVENT_STATE_PROGRESSION_AGGREGATION_ALGORITHM;

@Component(DEFAULT_EVENT_STATE_PROGRESSION_AGGREGATION_ALGORITHM)
@Slf4j
public class EventStateProgressionAggregationTopology
    extends
        BaseAggregationTopology<
                EventStateProgressionAggregationKey,
                EventStateProgressionEvent,
                EventStateProgressionEventAggregation>
    implements
        EventStateProgressionAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public EventStateProgressionEventAggregation constructEventAggregation(EventStateProgressionEvent event) {
        var aggEvent = new EventStateProgressionEventAggregation();
        aggEvent.setSource(event.getSource());
        aggEvent.setIntersectionID(event.getIntersectionID());
        aggEvent.setRoadRegulatorID(event.getRoadRegulatorID());
        aggEvent.setSignalGroupID(event.getSignalGroupID());
        aggEvent.setEventStateA(event.getEventStateA());
        aggEvent.setEventStateB(event.getEventStateB());
        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new EventStateProgressionEventAggregation().getEventType();
    }

    @Override
    public Class<EventStateProgressionAggregationKey> keyClass() {
        return EventStateProgressionAggregationKey.class;
    }

    @Override
    public Serde<EventStateProgressionAggregationKey> keySerde() {
        return JsonSerdes.EventStateProgressionAggregationKey();
    }

    @Override
    public Serde<EventStateProgressionEvent> eventSerde() {
        return JsonSerdes.EventStateProgressionEvent();
    }

    @Override
    public Serde<EventStateProgressionEventAggregation> eventAggregationSerde() {
        return JsonSerdes.EventStateProgressionEventAggregation();
    }

    @Override
    public StreamPartitioner<EventStateProgressionAggregationKey, EventStateProgressionEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }
}
