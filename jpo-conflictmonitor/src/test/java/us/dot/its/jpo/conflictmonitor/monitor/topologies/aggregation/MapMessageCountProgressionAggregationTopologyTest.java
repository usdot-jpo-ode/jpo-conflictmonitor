package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_message_count_progression.MapMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class MapMessageCountProgressionAggregationTopologyTest
    extends
        BaseAggregationTopologyTest<
                RsuIntersectionKey,
                MapMessageCountProgressionEvent,
                MapMessageCountProgressionAggregationKey,
                MapMessageCountProgressionEventAggregation,
                MapMessageCountProgressionAggregationTopology>{

    @Test
    public void testTopology() {
        List<KeyValue<MapMessageCountProgressionAggregationKey, MapMessageCountProgressionEventAggregation>> resultList
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
        return "topic.CmMapMessageCountProgressionEventAggregation";
    }

    @Override
    Serde<RsuIntersectionKey> eventKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<MapMessageCountProgressionEvent> eventSerde() {
        return JsonSerdes.MapMessageCountProgressionEvent();
    }

    @Override
    Serde<MapMessageCountProgressionAggregationKey> aggKeySerde() {
        return JsonSerdes.MapMessageCountProgressionAggregationKey();
    }

    @Override
    Serde<MapMessageCountProgressionEventAggregation> aggEventSerde() {
        return JsonSerdes.MapMessageCountProgressionEventAggregation();
    }

    @Override
    RsuIntersectionKey createKey() {
        return new RsuIntersectionKey(rsuId, intersectionId, region);
    }

    @Override
    MapMessageCountProgressionEvent createEvent() {
        var event = new MapMessageCountProgressionEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        event.setMessageType("MAP");
        event.setMessageCountA(1);
        event.setMessageCountB(2);
        event.setTimestampA(initialWallClock.plusMillis(1).toString());
        event.setTimestampB(initialWallClock.plusMillis(10).toString());
        return event;
    }

    @Override
    MapMessageCountProgressionAggregationTopology createTopology() {
        return new MapMessageCountProgressionAggregationTopology();
    }

    @Override
    KStream<MapMessageCountProgressionAggregationKey, MapMessageCountProgressionEvent>
    selectAggKey(KStream<RsuIntersectionKey, MapMessageCountProgressionEvent> instream) {
        return instream.selectKey((key, value) -> {
            var aggKey = new MapMessageCountProgressionAggregationKey();
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
