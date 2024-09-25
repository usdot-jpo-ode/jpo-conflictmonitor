package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.IllegalSpatTransitionEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.IllegalSpatTransitionNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.PhaseStateTransition;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatMovementState;
import us.dot.its.jpo.conflictmonitor.monitor.processors.SpatTransitionProcessor;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import java.time.Duration;
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

        final String movementStateStore = parameters.getMovementStateStoreName();
        final String latestTransitionStore = parameters.getLatestTransitionStoreName();
        final Duration retentionTime = Duration.ofMillis(parameters.getBufferTimeMs());

        builder.addStateStore(
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(movementStateStore, retentionTime),
                        JsonSerdes.RsuIntersectionSignalGroupKey(),
                        JsonSerdes.SpatMovementState()
                )
        );

        builder.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(latestTransitionStore),
                        JsonSerdes.RsuIntersectionSignalGroupKey(),
                        Serdes.Long()
                )
        );

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
            .process(() -> new SpatTransitionProcessor(parameters), movementStateStore, latestTransitionStore)
            // Pass only illegal transitions
            .filter(((rsuIntersectionSignalGroupKey, spatMovementStateTransition) -> {
                final PhaseStateTransition stateTransition = spatMovementStateTransition.getStateTransition();
                return parameters.getIllegalSpatTransitionList().contains(stateTransition);
            }))
            // Create IllegalSpatTransitionEvent
            .mapValues((key, stateTransition) -> {
                var event = new IllegalSpatTransitionEvent();
                event.setTransition(stateTransition);
                event.setSource(key.getRsuId());
                event.setIntersectionID(key.getIntersectionId());
                event.setRoadRegulatorID(key.getRegion());
                return event;
            });

        // Send events to topic
        signalGroupStates.to(parameters.getOutputTopicName(),
                Produced.with(
                        JsonSerdes.RsuIntersectionSignalGroupKey(),
                        JsonSerdes.IllegalSpatTransitionEvent(),
                        new IntersectionIdPartitioner<>()));

        // Send notifications to topic
        signalGroupStates
                .mapValues(event -> {
                    var notification = new IllegalSpatTransitionNotification();
                    notification.setEvent(event);
                    return notification;
                })
                .to(parameters.getNotificationTopicName(),
                        Produced.with(
                                JsonSerdes.RsuIntersectionSignalGroupKey(),
                                JsonSerdes.IllegalSpatTransitionNotification(),
                                new IntersectionIdPartitioner<>()));


    }
}
