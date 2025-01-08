package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.spat_message_count_progression.SpatMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class SpatMessageCountProgressionAggregationTopologyTest
    extends
        BaseAggregationTopologyTest<
                RsuIntersectionKey,
                SpatMessageCountProgressionEvent,
                SpatMessageCountProgressionAggregationKey,
                SpatMessageCountProgressionEventAggregation,
                SpatMessageCountProgressionAggregationTopology>{

    @Test
    public void testTopology() {
        List<KeyValue<SpatMessageCountProgressionAggregationKey, SpatMessageCountProgressionEventAggregation>> resultList
                = runTestTopology();
        assertThat("Should have produced 1 aggregated event", resultList, hasSize(1));
        var result = resultList.getFirst();
        log.info("Agg result: {}", result);
        var resultKey = result.key;
        assertThat(resultKey.getRsuId(), equalTo(rsuId));
        assertThat(resultKey.getIntersectionId(), equalTo(intersectionId));
        assertThat(resultKey.getRegion(), equalTo(region));
        var resultValue = result.value;
        assertThat(resultValue.getNumberOfEvents(), equalTo(numberOfEvents));
        var period = resultValue.getTimePeriod();
        assertThat(period, notNullValue());
        assertThat(period.getBeginTimestamp(), equalTo(initialWallClock.toEpochMilli()));
        assertThat(period.getEndTimestamp(), equalTo(initialWallClock.toEpochMilli() + intervalSeconds*1000));
    }

    @Override
    String outputTopicName() {
        return "topic.CmSpatMessageCountProgressionEventAggregation";
    }

    @Override
    Serde<RsuIntersectionKey> eventKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<SpatMessageCountProgressionEvent> eventSerde() {
        return JsonSerdes.SpatMessageCountProgressionEvent();
    }

    @Override
    Serde<SpatMessageCountProgressionAggregationKey> aggKeySerde() {
        return JsonSerdes.SpatMessageCountProgressionAggregationKey();
    }

    @Override
    Serde<SpatMessageCountProgressionEventAggregation> aggEventSerde() {
        return JsonSerdes.SpatMessageCountProgressionEventAggregation();
    }

    @Override
    RsuIntersectionKey createKey() {
        return new RsuIntersectionKey(rsuId, intersectionId, region);
    }

    @Override
    SpatMessageCountProgressionEvent createEvent() {
        var event = new SpatMessageCountProgressionEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        event.setMessageType("SPaT");
        event.setMessageCountA(1);
        event.setMessageCountB(2);
        event.setTimestampA(initialWallClock.plusMillis(1).toString());
        event.setTimestampB(initialWallClock.plusMillis(10).toString());
        return event;
    }

    @Override
    SpatMessageCountProgressionAggregationTopology createTopology() {
        return new SpatMessageCountProgressionAggregationTopology();
    }

    @Override
    KStream<SpatMessageCountProgressionAggregationKey, SpatMessageCountProgressionEvent>
    selectAggKey(KStream<RsuIntersectionKey, SpatMessageCountProgressionEvent> instream) {
        return instream.selectKey((key, value) -> {
            var aggKey = new SpatMessageCountProgressionAggregationKey();
            aggKey.setRsuId(key.getRsuId());
            aggKey.setIntersectionId(key.getIntersectionId());
            aggKey.setRegion(key.getRegion());
            // TODO
//            aggKey.setDataFrame(value.getDataFrame());
//            aggKey.setChange(value.getChange());
            return aggKey;
        });
    }
}
