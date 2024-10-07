package us.dot.its.jpo.conflictmonitor.monitor.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.query.MultiVersionedKeyQuery;
import org.apache.kafka.streams.query.PositionBound;
import org.apache.kafka.streams.query.QueryConfig;
import org.apache.kafka.streams.query.QueryResult;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.VersionedKeyValueStore;
import org.apache.kafka.streams.state.VersionedRecord;
import org.apache.kafka.streams.state.VersionedRecordIterator;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event_state_progression.EventStateProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.SpatMovementState;
import us.dot.its.jpo.conflictmonitor.monitor.models.event_state_progression.SpatMovementStateTransition;

import java.time.Instant;

@Slf4j
public class SpatTransitionProcessor
        extends ContextualProcessor<RsuIntersectionSignalGroupKey, SpatMovementState, RsuIntersectionSignalGroupKey, SpatMovementStateTransition> {

    // Store to keep track of the latest Spat MovementState per signal group
    VersionedKeyValueStore<RsuIntersectionSignalGroupKey, SpatMovementState> stateStore;
    KeyValueStore<RsuIntersectionSignalGroupKey, Long> latestTransitionStore;

    final EventStateProgressionParameters parameters;

    public SpatTransitionProcessor(EventStateProgressionParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void init(ProcessorContext<RsuIntersectionSignalGroupKey, SpatMovementStateTransition> context) {
        super.init(context);
        stateStore = context.getStateStore(parameters.getMovementStateStoreName());
        latestTransitionStore = context.getStateStore(parameters.getLatestTransitionStoreName());
    }

    @Override
    public void process(Record<RsuIntersectionSignalGroupKey, SpatMovementState> record) {

        if (parameters.isDebug()) {
            log.trace("Received record: timestamp {}, signal group {}, phase {}", record.timestamp(), record.key().getSignalGroup(),
                    record.value().getPhaseState());
        }

        // Insert new record into the buffer
        stateStore.put(record.key(), record.value(), record.timestamp());

        // Query the buffer, excluding the grace period relative to stream time "now".
        Instant excludeGracePeriod =
                Instant.ofEpochMilli(context().currentStreamTimeMs())
                        .minusMillis(parameters.getBufferGracePeriodMs());



        // Start query at the latest transition point to avoid duplicates
        Long latestTransitionTime = latestTransitionStore.get(record.key());
        Instant startTime;
        if (latestTransitionTime != null) {
           startTime = Instant.ofEpochMilli(latestTransitionTime);
        } else {
            // No transitions yet, base start time on time window
            startTime = Instant.ofEpochMilli(context().currentStreamTimeMs())
                    .minusMillis(parameters.getBufferTimeMs());
        }
        var query =
                    MultiVersionedKeyQuery.<RsuIntersectionSignalGroupKey, SpatMovementState>withKey(record.key())
                            .fromTime(startTime)
                            .toTime(excludeGracePeriod)
                            .withAscendingTimestamps();


        QueryResult<VersionedRecordIterator<SpatMovementState>> result =
                stateStore.query(query,
                        PositionBound.unbounded(),
                        new QueryConfig(false));

        if (result.isSuccess()) {

            // Identify transitions, and forward transition messages
            VersionedRecordIterator<SpatMovementState> iterator = result.getResult();
            SpatMovementState previousState = null;

            while (iterator.hasNext()) {
                final VersionedRecord<SpatMovementState> state = iterator.next();
                final SpatMovementState thisState = state.value();
                if (previousState != null && previousState.getPhaseState() != thisState.getPhaseState()) {

                    if (parameters.isDebug()) {
                        log.info("transition detected at timestamp {} -> {}, signal group {}, {} -> {}",
                                previousState.getUtcTimeStamp(),state.timestamp(),
                                record.key().getSignalGroup(), previousState.getPhaseState(), thisState.getPhaseState());
                    }

                    latestTransitionStore.put(record.key(), record.timestamp());

                    // Transition detected,
                    context().forward(record
                            .withTimestamp(state.timestamp())
                            .withValue(new SpatMovementStateTransition(previousState, thisState)));

                }
                previousState = thisState;
            }
        } else {
            log.error("Failed to query state store: {}", result.getFailureMessage());
        }

    }

    @Override
    public void close() {
        super.close();
    }
}
