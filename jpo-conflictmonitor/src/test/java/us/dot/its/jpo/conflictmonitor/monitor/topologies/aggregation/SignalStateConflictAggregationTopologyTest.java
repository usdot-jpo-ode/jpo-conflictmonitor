package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;

import org.junit.Test;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalStateConflictAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedMovementPhaseState;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class SignalStateConflictAggregationTopologyTest
    extends
        BaseAggregationTopologyTest<
                RsuIntersectionKey,
                SignalStateConflictEvent,
                SignalStateConflictAggregationKey,
                SignalStateConflictEventAggregation,
                SignalStateConflictAggregationTopology
                >{

    final int signalGroupA = 5;
    final int signalGroupB = 10;
    final ProcessedMovementPhaseState stateA = ProcessedMovementPhaseState.PROTECTED_MOVEMENT_ALLOWED;
    final ProcessedMovementPhaseState stateB = ProcessedMovementPhaseState.STOP_AND_REMAIN;

    @Test
    public void testTopology() {
        List<KeyValue<SignalStateConflictAggregationKey, SignalStateConflictEventAggregation>> resultList
                = runTestTopology();
        assertThat("Should have produced 1 aggregated event", resultList, hasSize(1));
        var result = resultList.getFirst();
        log.info("Agg result: {}", result);
        var resultKey = result.key;
        assertThat(resultKey.getRsuId(), equalTo(rsuId));
        assertThat(resultKey.getIntersectionId(), equalTo(intersectionId));
        assertThat(resultKey.getRegion(), equalTo(region));
        assertThat(resultKey.getConflictingSignalGroupA(), equalTo(signalGroupA));
        assertThat(resultKey.getConflictingSignalGroupB(), equalTo(signalGroupB));
        assertThat(resultKey.getEventStateA(), equalTo(stateA));
        assertThat(resultKey.getEventStateB(), equalTo(stateB));
        var resultValue = result.value;
        assertThat(resultValue.getNumberOfEvents(), equalTo(numberOfEvents));
        assertThat(resultValue.getEventStateA(), equalTo(stateA));
        assertThat(resultValue.getEventStateB(), equalTo(stateB));
        assertThat(resultValue.getConflictingSignalGroupA(), equalTo(signalGroupA));
        assertThat(resultValue.getConflictingSignalGroupB(), equalTo(signalGroupB));
        var period = resultValue.getTimePeriod();
        assertThat(period, notNullValue());
        assertThat(period.getBeginTimestamp(), equalTo(initialWallClock.toEpochMilli()));
        assertThat(period.getEndTimestamp(), equalTo(initialWallClock.toEpochMilli() + intervalSeconds*1000));
    }

    @Override
    SignalStateConflictEvent createEvent() {
        var event = new SignalStateConflictEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        // All events have the same values
        event.setFirstConflictingSignalGroup(signalGroupA);
        event.setSecondConflictingSignalGroup(signalGroupB);
        event.setFirstConflictingSignalState(stateA);
        event.setSecondConflictingSignalState(stateB);
        return event;
    }

    @Override
    String outputTopicName() {
        return "topic.CmSignalStateConflictEventAggregation";
    }

    @Override
    Serde<RsuIntersectionKey> eventKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<SignalStateConflictEvent> eventSerde() {
        return JsonSerdes.SignalStateConflictEvent();
    }

    @Override
    Serde<SignalStateConflictAggregationKey> aggKeySerde() {
        return JsonSerdes.SignalStateConflictAggregationKey();
    }

    @Override
    Serde<SignalStateConflictEventAggregation> aggEventSerde() {
        return JsonSerdes.SignalStateConflictEventAggregation();
    }

    @Override
    RsuIntersectionKey createKey() {
        return new RsuIntersectionKey(rsuId, intersectionId, region);
    }

    @Override
    SignalStateConflictAggregationTopology createTopology() {
        return new SignalStateConflictAggregationTopology();
    }

    @Override
    KStream<SignalStateConflictAggregationKey, SignalStateConflictEvent>
    selectAggKey(KStream<RsuIntersectionKey, SignalStateConflictEvent> instream) {
        return instream.selectKey((key, value) -> {
            var aggKey = new SignalStateConflictAggregationKey();
            aggKey.setRsuId(key.getRsuId());
            aggKey.setIntersectionId(key.getIntersectionId());
            aggKey.setRegion(key.getRegion());
            aggKey.setEventStateA(value.getFirstConflictingSignalState());
            aggKey.setEventStateB(value.getSecondConflictingSignalState());
            aggKey.setConflictingSignalGroupA(value.getFirstConflictingSignalGroup());
            aggKey.setConflictingSignalGroupB(value.getSecondConflictingSignalGroup());
            return aggKey;
        });
    }
}
