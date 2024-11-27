package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.checkerframework.checker.units.qual.A;
import us.dot.its.jpo.conflictmonitor.monitor.models.RegulatorIntersectionId;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class IntersectionReferenceAlignmentAggregationTopologyTest
        extends BaseAggregationTopologyTest<
            String,
            IntersectionReferenceAlignmentEvent,
            String,
            IntersectionReferenceAlignmentEventAggregation,
            IntersectionReferenceAlignmentAggregationTopology>{

    @Override
    String outputTopicName() {
        return "topic.CmIntersectionReferenceAlignmentEventAggregation";
    }

    @Override
    Serde<String> eventKeySerde() {
        return Serdes.String();
    }

    @Override
    Serde<IntersectionReferenceAlignmentEvent> eventSerde() {
        return JsonSerdes.IntersectionReferenceAlignmentEvent();
    }

    @Override
    Serde<String> aggKeySerde() {
        return Serdes.String();
    }

    @Override
    Serde<IntersectionReferenceAlignmentEventAggregation> aggEventSerde() {
        return JsonSerdes.IntersectionReferenceAlignmentEventAggregation();
    }

    @Override
    String createKey() {
        return rsuId;
    }

    final AtomicInteger mapNum = new AtomicInteger(1);
    final AtomicInteger spatNum = new AtomicInteger(1000000);

    @Override
    IntersectionReferenceAlignmentEvent createEvent() {
        var event = new IntersectionReferenceAlignmentEvent();
        event.setSource(rsuId);

        // Add a different set of conflicting map/spat intersections for each event
        event.setMapRegulatorIntersectionIds(getNextRegulatorIntersectionSet(mapNum));
        event.setSpatRegulatorIntersectionIds(getNextRegulatorIntersectionSet(spatNum));

        return event;
    }

    Set<RegulatorIntersectionId> getNextRegulatorIntersectionSet(AtomicInteger num) {
        final int id = num.getAndIncrement();
        var regionIntId = new RegulatorIntersectionId();
        regionIntId.setIntersectionId(id);
        regionIntId.setRoadRegulatorId(id);
        return Set.of(regionIntId);
    }

    @Override
    IntersectionReferenceAlignmentAggregationTopology createTopology() {
        return new IntersectionReferenceAlignmentAggregationTopology();
    }

    @Override
    KStream<String, IntersectionReferenceAlignmentEvent> selectAggKey(KStream<String, IntersectionReferenceAlignmentEvent> instream) {
        // Same key, just pass through
        return instream;
    }
}
