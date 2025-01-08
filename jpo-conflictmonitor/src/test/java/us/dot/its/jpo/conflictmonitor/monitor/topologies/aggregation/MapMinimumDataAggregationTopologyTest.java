package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
public class MapMinimumDataAggregationTopologyTest
    extends
        BaseAggregationTopologyTest<
                RsuIntersectionKey,
                MapMinimumDataEvent,
                RsuIntersectionKey,
                MapMinimumDataEventAggregation,
                MapMinimumDataAggregationTopology>{

    @Test
    public void testTopology() {
        List<KeyValue<RsuIntersectionKey, MapMinimumDataEventAggregation>> resultList = runTestTopology();
        assertThat("Should have produced 1 aggregated event", resultList, hasSize(1));
        var result = resultList.getFirst();
        log.info("Agg result: {}", result);
        var resultKey = result.key;
        assertThat(resultKey, equalTo(createKey()));
        var resultValue = result.value;
        assertThat(resultValue.getNumberOfEvents(), equalTo(numberOfEvents));
        assertThat(resultValue.getMissingDataElements(), hasSize(numberOfEvents));
        var period = resultValue.getTimePeriod();
        assertThat(period, notNullValue());
        assertThat(period.getBeginTimestamp(), equalTo(initialWallClock.toEpochMilli()));
        assertThat(period.getEndTimestamp(), equalTo(initialWallClock.toEpochMilli() + intervalSeconds*1000));
    }

    @Override
    String outputTopicName() {
        return "topic.CmMapMinimumDataEventAggregation";
    }

    @Override
    Serde<RsuIntersectionKey> eventKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<MapMinimumDataEvent> eventSerde() {
        return JsonSerdes.MapMinimumDataEvent();
    }

    @Override
    Serde<RsuIntersectionKey> aggKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<MapMinimumDataEventAggregation> aggEventSerde() {
        return JsonSerdes.MapMinimumDataEventAggregation();
    }

    @Override
    RsuIntersectionKey createKey() {
        return new RsuIntersectionKey(rsuId, intersectionId, region);
    }

    final AtomicInteger elementNum = new AtomicInteger(1);

    @Override
    MapMinimumDataEvent createEvent() {
        var event = new MapMinimumDataEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        int element = elementNum.getAndIncrement();
        event.setMissingDataElements(List.of(Integer.toString(element)));
        return event;
    }

    @Override
    MapMinimumDataAggregationTopology createTopology() {
        return new MapMinimumDataAggregationTopology();
    }

    @Override
    KStream<RsuIntersectionKey, MapMinimumDataEvent> selectAggKey(KStream<RsuIntersectionKey, MapMinimumDataEvent> instream) {
        // Same key, pass through
        return instream;
    }
}
