package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalStateConflictAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEventAggregation;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

public class SignalStateConflictAggregationTopologyTest
    extends
        BaseAggregationTopologyTest<
                RsuIntersectionKey,
                SignalStateConflictEvent,
                SignalStateConflictAggregationKey,
                SignalStateConflictEventAggregation,
                SignalStateConflictAggregationTopology
                >{


    @Override
    String outputTopicName() {
        return "";
    }

    @Override
    Serde<RsuIntersectionKey> eventKeySerde() {
        return null;
    }

    @Override
    Serde<SignalStateConflictEvent> eventSerde() {
        return null;
    }

    @Override
    Serde<SignalStateConflictAggregationKey> aggKeySerde() {
        return null;
    }

    @Override
    Serde<SignalStateConflictEventAggregation> aggEventSerde() {
        return null;
    }

    @Override
    RsuIntersectionKey createKey() {
        return null;
    }

    @Override
    SignalStateConflictEvent createEvent() {
        return null;
    }

    @Override
    SignalStateConflictAggregationTopology createTopology() {
        return null;
    }

    @Override
    KStream<SignalStateConflictAggregationKey, SignalStateConflictEvent> selectAggKey(KStream<RsuIntersectionKey, SignalStateConflictEvent> instream) {
        return null;
    }
}
