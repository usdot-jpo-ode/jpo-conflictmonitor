package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalStateConflictAggregationKey;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.map_spat_message_assessment.SignalStateConflictAggregationStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateConflictEventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationConstants.DEFAULT_SIGNAL_STATE_CONFLICT_AGGREGATION_ALGORITHM;

@Component(DEFAULT_SIGNAL_STATE_CONFLICT_AGGREGATION_ALGORITHM)
@Slf4j
public class SignalStateConflictAggregationTopology
    extends
        BaseAggregationTopology<
                SignalStateConflictAggregationKey,
                SignalStateConflictEvent,
                SignalStateConflictEventAggregation>
    implements
        SignalStateConflictAggregationStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public SignalStateConflictEventAggregation constructEventAggregation(SignalStateConflictEvent event) {
        var aggEvent = new SignalStateConflictEventAggregation();
        aggEvent.setSource(event.getSource());
        aggEvent.setIntersectionID(event.getIntersectionID());
        aggEvent.setRoadRegulatorID(event.getRoadRegulatorID());
        aggEvent.setConflictingSignalGroupA(event.getFirstConflictingSignalGroup());
        aggEvent.setEventStateA(event.getFirstConflictingSignalState());
        aggEvent.setConflictingSignalGroupB(event.getSecondConflictingSignalGroup());
        aggEvent.setEventStateB(event.getSecondConflictingSignalState());
        return aggEvent;
    }

    @Override
    public String eventAggregationType() {
        return new SignalStateConflictEventAggregation().getEventType();
    }

    @Override
    public Class<SignalStateConflictAggregationKey> keyClass() {
        return SignalStateConflictAggregationKey.class;
    }

    @Override
    public Serde<SignalStateConflictAggregationKey> keySerde() {
        return JsonSerdes.SignalStateConflictAggregationKey();
    }

    @Override
    public Serde<SignalStateConflictEvent> eventSerde() {
        return JsonSerdes.SignalStateConflictEvent();
    }

    @Override
    public Serde<SignalStateConflictEventAggregation> eventAggregationSerde() {
        return JsonSerdes.SignalStateConflictEventAggregation();
    }

    @Override
    public StreamPartitioner<SignalStateConflictAggregationKey, SignalStateConflictEventAggregation> eventAggregationPartitioner() {
        return new IntersectionIdPartitioner<>();
    }
}
