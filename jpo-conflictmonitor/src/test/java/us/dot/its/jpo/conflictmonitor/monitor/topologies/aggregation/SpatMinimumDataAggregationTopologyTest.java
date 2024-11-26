package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.Topology;

import org.junit.Test;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class SpatMinimumDataAggregationTopologyTest
    extends
        BaseAggregationTopologyTest<
                RsuIntersectionKey,
                SpatMinimumDataEvent,
                RsuIntersectionKey,
                SpatMinimumDataEventAggregation,
                SpatMinimumDataAggregationTopology> {

    @Override
    String outputTopicName() {
        return "topic.CmSpatMinimumDataEventAggregation";
    }

    @Override
    Serde<RsuIntersectionKey> eventKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<SpatMinimumDataEvent> eventSerde() {
        return JsonSerdes.SpatMinimumDataEvent();
    }

    @Override
    Serde<RsuIntersectionKey> aggKeySerde() {
        return us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
    }

    @Override
    Serde<SpatMinimumDataEventAggregation> aggEventSerde() {
        return JsonSerdes.SpatMinimumDataEventAggregation();
    }

    @Override
    SpatMinimumDataAggregationTopology createTopology() {
        return new SpatMinimumDataAggregationTopology();
    }


    @Test
    public void testTopology() {
        List<KeyValue<RsuIntersectionKey, SpatMinimumDataEventAggregation>> resultList = runTestTopology();
        assertThat("Should have produced 1 aggregated event", resultList, hasSize(1));
        var result = resultList.getFirst();
        log.info("Agg result: {}", result);
        var resultKey = result.key;
        assertThat(resultKey, equalTo(createKey()));
        var resultValue = result.value;
        assertThat(resultValue.getNumberOfEvents(), equalTo(numberOfEvents));
        assertThat(resultValue.getMissingDataElements(), hasSize(numberOfEvents));
    }

    @Override
    RsuIntersectionKey createKey() {
        return new RsuIntersectionKey(rsuId, intersectionId, region);
    }

    final AtomicInteger elementNum = new AtomicInteger(1);

    @Override
    SpatMinimumDataEvent createEvent() {
        var event = new SpatMinimumDataEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        int element = elementNum.getAndIncrement();
        event.setMissingDataElements(List.of(Integer.toString(element)));
        return event;
    }



}
