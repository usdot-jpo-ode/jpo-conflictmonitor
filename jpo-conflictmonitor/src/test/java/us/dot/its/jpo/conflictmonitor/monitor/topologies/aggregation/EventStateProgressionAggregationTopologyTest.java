package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.event_state_progression.EventStateProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventStateProgressionEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
public class EventStateProgressionAggregationTopologyTest
    extends
        BaseAggregationTopologyTest<
                RsuIntersectionKey,
                EventStateProgressionEvent,
                EventStateProgressionAggregationKey,
                EventStateProgressionEventAggregation,
                EventStateProgressionAggregationTopology>{

    final int signalGroup = 6;
    final J2735MovementPhaseState eventStateA = J2735MovementPhaseState.STOP_THEN_PROCEED;
    final J2735MovementPhaseState eventStateB = J2735MovementPhaseState.PERMISSIVE_CLEARANCE;

    @Test
    public void testTopology() {
        List<KeyValue<EventStateProgressionAggregationKey, EventStateProgressionEventAggregation>> resultList = runTestTopology();
        assertThat("Should have produced 1 aggregated event", resultList, hasSize(1));
        var result = resultList.getFirst();
        log.info("Agg result: {}", result);
        var resultKey = result.key;
        assertThat(resultKey.getRsuId(), equalTo(rsuId));
        assertThat(resultKey.getIntersectionId(), equalTo(intersectionId));
        assertThat(resultKey.getRegion(), equalTo(region));
        assertThat(resultKey.getSignalGroup(), equalTo(signalGroup));
        assertThat(resultKey.getEventStateA(), equalTo(eventStateA));
        assertThat(resultKey.getEventStateB(), equalTo(eventStateB));
        var resultValue = result.value;
        assertThat(resultValue.getNumberOfEvents(), equalTo(numberOfEvents));
        assertThat(resultValue.getEventStateA(), equalTo(eventStateA));
        assertThat(resultValue.getEventStateB(), equalTo(eventStateB));
        assertThat(resultValue.getSignalGroupID(), equalTo(signalGroup));
        var period = resultValue.getTimePeriod();
        assertThat(period, notNullValue());
        assertThat(period.getBeginTimestamp(), equalTo(initialWallClock.toEpochMilli()));
        assertThat(period.getEndTimestamp(), equalTo(initialWallClock.toEpochMilli() + intervalSeconds*1000));
    }

    @Override
    EventStateProgressionEvent createEvent() {
        var event = new EventStateProgressionEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        event.setSignalGroupID(signalGroup);
        event.setTimestampA(initialWallClock.toEpochMilli() + 1);
        event.setTimestampB(initialWallClock.toEpochMilli() + 10);
        event.setEventStateA(J2735MovementPhaseState.STOP_THEN_PROCEED);
        event.setEventStateB(J2735MovementPhaseState.PERMISSIVE_CLEARANCE);
        return event;
    }

    @Override
    String outputTopicName() {
        return "topic.CmEventStateProgressionEventAggregation";
    }

    @Override
    Serde<RsuIntersectionKey> eventKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<EventStateProgressionEvent> eventSerde() {
        return JsonSerdes.EventStateProgressionEvent();
    }

    @Override
    Serde<EventStateProgressionAggregationKey> aggKeySerde() {
        return JsonSerdes.EventStateProgressionAggregationKey();
    }

    @Override
    Serde<EventStateProgressionEventAggregation> aggEventSerde() {
        return JsonSerdes.EventStateProgressionEventAggregation();
    }

    @Override
    RsuIntersectionKey createKey() {
        return new RsuIntersectionKey(rsuId, intersectionId, region);
    }



    @Override
    EventStateProgressionAggregationTopology createTopology() {
        return new EventStateProgressionAggregationTopology();
    }

    @Override
    KStream<EventStateProgressionAggregationKey, EventStateProgressionEvent>
    selectAggKey(KStream<RsuIntersectionKey, EventStateProgressionEvent> instream) {
        return instream.selectKey((key, value) -> {
            var aggKey = new EventStateProgressionAggregationKey();
            aggKey.setRsuId(key.getRsuId());
            aggKey.setIntersectionId(key.getIntersectionId());
            aggKey.setRegion(key.getRegion());
            aggKey.setSignalGroup(value.getSignalGroupID());
            aggKey.setEventStateA(value.getEventStateA());
            aggKey.setEventStateB(value.getEventStateB());
            return aggKey;
        });
    }
}
