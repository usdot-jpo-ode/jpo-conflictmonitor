package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEventAggregation;

public class SignalGroupAlignmentAggregationTopologyTest
        extends BaseAggregationTopologyTest<
            String,
            IntersectionReferenceAlignmentEvent,
            String,
            IntersectionReferenceAlignmentEventAggregation,
            IntersectionReferenceAlignmentAggregationTopology>{

    @Override
    String outputTopicName() {
        return "";
    }

    @Override
    Serde<String> eventKeySerde() {
        return null;
    }

    @Override
    Serde<IntersectionReferenceAlignmentEvent> eventSerde() {
        return null;
    }

    @Override
    Serde<String> aggKeySerde() {
        return null;
    }

    @Override
    Serde<IntersectionReferenceAlignmentEventAggregation> aggEventSerde() {
        return null;
    }

    @Override
    String createKey() {
        return "";
    }

    @Override
    IntersectionReferenceAlignmentEvent createEvent() {
        return null;
    }

    @Override
    IntersectionReferenceAlignmentAggregationTopology createTopology() {
        return null;
    }

    @Override
    KStream<String, IntersectionReferenceAlignmentEvent> selectAggKey(KStream<String, IntersectionReferenceAlignmentEvent> instream) {
        return null;
    }
}
