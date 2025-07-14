package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalGroupAlignmentEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
public class SignalGroupAlignmentAggregationTopologyTest
        extends
            BaseAggregationTopologyTest<
                    RsuIntersectionKey,
                    SignalGroupAlignmentEvent,
                    RsuIntersectionKey,
                    SignalGroupAlignmentEventAggregation,
                    SignalGroupAlignmentAggregationTopology>   {

    @Test
    public void testTopology() {
        List<KeyValue<RsuIntersectionKey, SignalGroupAlignmentEventAggregation>> resultList = runTestTopology();
        assertThat("Should have produced 1 aggregated event", resultList, hasSize(1));
        var result = resultList.getFirst();
        log.info("Agg result: {}", result);
        var resultKey = result.key;
        assertThat(resultKey, equalTo(createKey()));
        var resultValue = result.value;
        assertThat(resultValue.getNumberOfEvents(), equalTo(numberOfEvents));
        assertThat(resultValue.getMapSignalGroupIds(), hasSize(numberOfEvents));
        assertThat(resultValue.getSpatSignalGroupIds(), hasSize(numberOfEvents));
        var period = resultValue.getTimePeriod();
        assertThat(period, notNullValue());
        assertThat(period.getBeginTimestamp(), equalTo(initialWallClock.toEpochMilli()));
        assertThat(period.getEndTimestamp(), equalTo(initialWallClock.toEpochMilli() + intervalSeconds*1000));
    }

    @Override
    String outputTopicName() {
        return "topic.CmSignalGroupAlignmentEventAggregation";
    }

    @Override
    Serde<RsuIntersectionKey> eventKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<SignalGroupAlignmentEvent> eventSerde() {
        return JsonSerdes.SignalGroupAlignmentEvent();
    }

    @Override
    Serde<RsuIntersectionKey> aggKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<SignalGroupAlignmentEventAggregation> aggEventSerde() {
        return JsonSerdes.SignalGroupAlignmentEventAggregation();
    }

    @Override
    RsuIntersectionKey createKey() {
        return new RsuIntersectionKey(rsuId, intersectionId, region);
    }

    final AtomicInteger mapSignalGroup = new AtomicInteger(1);
    final AtomicInteger spatSignalGroup = new AtomicInteger(20);

    @Override
    SignalGroupAlignmentEvent createEvent() {
        var event = new SignalGroupAlignmentEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        // Unique conflicting signal groups for each event
        event.setMapSignalGroupIds(Set.of(mapSignalGroup.getAndIncrement()));
        event.setSpatSignalGroupIds(Set.of(spatSignalGroup.getAndIncrement()));
        return event;
    }

    @Override
    SignalGroupAlignmentAggregationTopology createTopology() {
        return new SignalGroupAlignmentAggregationTopology();
    }

    @Override
    KStream<RsuIntersectionKey, SignalGroupAlignmentEvent> selectAggKey(KStream<RsuIntersectionKey, SignalGroupAlignmentEvent> instream) {
        // Same key, pass through
        return instream;
    }
}
