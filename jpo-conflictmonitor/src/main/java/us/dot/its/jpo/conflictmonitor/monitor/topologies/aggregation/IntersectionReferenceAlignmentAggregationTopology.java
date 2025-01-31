package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.IntersectionReferenceAlignmentAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEventAggregation;

import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;


import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_INTERSECTION_REFERENCE_ALIGNMENT_AGGREGATION_ALGORITHM;

@Component(DEFAULT_INTERSECTION_REFERENCE_ALIGNMENT_AGGREGATION_ALGORITHM)
@Slf4j
public class IntersectionReferenceAlignmentAggregationTopology
    extends
        BaseAggregationTopology<
                String,
                IntersectionReferenceAlignmentEvent,
                IntersectionReferenceAlignmentEventAggregation>
    implements
        IntersectionReferenceAlignmentAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public IntersectionReferenceAlignmentEventAggregation constructEventAggregation(IntersectionReferenceAlignmentEvent event) {
        var aggEvent = new IntersectionReferenceAlignmentEventAggregation();
        aggEvent.setSource(event.getSource());
        // Don't set Intersection ID or Road Regulator because they are not part of the key
        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new IntersectionReferenceAlignmentEventAggregation().getEventType();
    }

    @Override
    public Class<String> keyClass() {
        return String.class;
    }

    @Override
    public Serde<String> keySerde() {
        return Serdes.String();
    }

    @Override
    public Serde<IntersectionReferenceAlignmentEvent> eventSerde() {
        return JsonSerdes.IntersectionReferenceAlignmentEvent();
    }

    @Override
    public Serde<IntersectionReferenceAlignmentEventAggregation> eventAggregationSerde() {
        return JsonSerdes.IntersectionReferenceAlignmentEventAggregation();
    }

    @Override
    public StreamPartitioner<String, IntersectionReferenceAlignmentEventAggregation> eventAggregationPartitioner() {
        // Will use default partitioner because String doesn't implement the interface
        return new IntersectionIdPartitioner<>();
    }
}
