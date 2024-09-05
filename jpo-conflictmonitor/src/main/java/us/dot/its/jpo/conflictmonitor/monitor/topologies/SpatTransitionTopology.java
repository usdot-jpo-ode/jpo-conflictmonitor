package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Repartitioned;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.spat_transition.IllegalSpatTransitionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.PhaseStateTransition;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatMovementState;
import us.dot.its.jpo.conflictmonitor.monitor.processors.SpatTransitionProcessor;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import java.util.List;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionConstants.DEFAULT_SPAT_TRANSITION_ALGORITHM;

@Component(DEFAULT_SPAT_TRANSITION_ALGORITHM)
@Slf4j
public class SpatTransitionTopology
    extends BaseStreamsBuilder<SpatTransitionParameters>
    implements SpatTransitionStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, ProcessedSpat> inputStream) {

        var signalGroupStates = inputStream
                // Ignore tombstones
                .filter(((rsuIntersectionKey, processedSpat) -> processedSpat != null))

                // Extract all the signal group MovementStates from a ProcessedSpat
                .flatMap((rsuIntersectionKey, processedSpat) -> {
                    List<SpatMovementState> states = SpatMovementState.fromProcessedSpat(processedSpat);

                    // Add signal group to key
                    // Return type List<KeyValue<RsuIntersectionSignalGroupKey, SpatMovementState>>
                    return states.stream().map(state
                                -> new KeyValue<>(
                                        new RsuIntersectionSignalGroupKey(rsuIntersectionKey, state.getSignalGroup()),
                                        state)).toList();
                })
                // Find phase state transitions
                .process(() -> new SpatTransitionProcessor(parameters))
                // Filter only illegal transitions
                .filter(((rsuIntersectionSignalGroupKey, spatMovementStateTransition) -> {
                    final PhaseStateTransition stateTransition = spatMovementStateTransition.getStateTransition();
                    return parameters.getIllegalSpatTransitionList().contains(stateTransition);
                }))
                // Create IllegalSpatTransitionEvent
                .mapValues(stateTransition -> {
                    var event = new IllegalSpatTransitionEvent();

                    return event;
                });


    }
}
