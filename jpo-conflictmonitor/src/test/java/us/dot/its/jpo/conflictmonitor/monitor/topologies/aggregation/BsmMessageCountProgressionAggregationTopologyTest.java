package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.bsm_message_count_progression.BsmMessageCountProgressionAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class BsmMessageCountProgressionAggregationTopologyTest
    extends
        BaseAggregationTopologyTest<
                String,
                BsmMessageCountProgressionEvent,
                BsmMessageCountProgressionAggregationKey,
                BsmMessageCountProgressionEventAggregation,
                BsmMessageCountProgressionAggregationTopology>{

    @Test
    public void testTopology() {
        List<KeyValue<BsmMessageCountProgressionAggregationKey, BsmMessageCountProgressionEventAggregation>> resultList
                = runTestTopology();
        assertThat("Should have produced 1 aggregated event", resultList, hasSize(1));
        var result = resultList.getFirst();
        log.info("Agg result: {}", result);
        var resultKey = result.key;
        assertThat(resultKey.getRsuId(), equalTo(rsuId));
        var resultValue = result.value;
        assertThat(resultValue.getNumberOfEvents(), equalTo(numberOfEvents));
        var period = resultValue.getTimePeriod();
        assertThat(period, notNullValue());
        assertThat(period.getBeginTimestamp(), equalTo(initialWallClock.toEpochMilli()));
        assertThat(period.getEndTimestamp(), equalTo(initialWallClock.toEpochMilli() + intervalSeconds*1000));
    }

    @Override
    String outputTopicName() {
        return "topic.CmBsmMessageCountProgressionEventAggregation";
    }

    @Override
    Serde<String> eventKeySerde() {
        return Serdes.String();
    }

    @Override
    Serde<BsmMessageCountProgressionEvent> eventSerde() {
        return JsonSerdes.BsmMessageCountProgressionEvent();
    }

    @Override
    Serde<BsmMessageCountProgressionAggregationKey> aggKeySerde() {
        return JsonSerdes.BsmMessageCountProgressionAggregationKey();
    }

    @Override
    Serde<BsmMessageCountProgressionEventAggregation> aggEventSerde() {
        return JsonSerdes.BsmMessageCountProgressionEventAggregation();
    }

    @Override
    String createKey() {
        return rsuId;
    }

    @Override
    BsmMessageCountProgressionEvent createEvent() {
        var event = new BsmMessageCountProgressionEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        event.setMessageType("BSM");
        event.setMessageCountA(1);
        event.setMessageCountB(2);
        event.setTimestampA(initialWallClock.plusMillis(1).toString());
        event.setTimestampB(initialWallClock.plusMillis(10).toString());
        return event;
    }

    @Override
    BsmMessageCountProgressionAggregationTopology createTopology() {
        return new BsmMessageCountProgressionAggregationTopology();
    }

    @Override
    KStream<BsmMessageCountProgressionAggregationKey, BsmMessageCountProgressionEvent>
    selectAggKey(KStream<String, BsmMessageCountProgressionEvent> instream) {
        return instream.selectKey((key, value) -> {
            var aggKey = new BsmMessageCountProgressionAggregationKey();
            aggKey.setRsuId(key);
            // TODO
//            aggKey.setDataFrame(value.getDataFrame());
//            aggKey.setChange(value.getChange());
            return aggKey;
        });
    }
}
