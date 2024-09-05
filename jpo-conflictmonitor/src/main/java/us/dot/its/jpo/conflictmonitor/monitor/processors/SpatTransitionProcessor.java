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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_transition.SpatTransitionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.RsuIntersectionSignalGroupKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatMovementState;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat_transition.SpatMovementStateTransition;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SpatTransitionProcessor
        extends ContextualProcessor<RsuIntersectionSignalGroupKey, SpatMovementState, RsuIntersectionSignalGroupKey, SpatMovementStateTransition> {

    // Store to keep track of the latest Spat MovementState per signal group
    VersionedKeyValueStore<RsuIntersectionSignalGroupKey, SpatMovementState> stateStore;
    KeyValueStore<RsuIntersectionSignalGroupKey, SpatMovementState> keyStore;

    final SpatTransitionParameters parameters;

    public SpatTransitionProcessor(SpatTransitionParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void init(ProcessorContext<RsuIntersectionSignalGroupKey, SpatMovementStateTransition> context) {
        super.init(context);
        stateStore = context.getStateStore(parameters.getMovementStateStoreName());
    }

    @Override
    public void process(Record<RsuIntersectionSignalGroupKey, SpatMovementState> record) {
        // Insert into the buffer
        stateStore.put(record.key(), record.value(), record.timestamp());

        // Query the buffer, excluding the grace period
        Instant excludeGracePeriod =
                Instant.ofEpochMilli(record.timestamp())
                        .minusMillis(parameters.getBufferGracePeriodMs());
        var query =
                MultiVersionedKeyQuery.<RsuIntersectionSignalGroupKey, SpatMovementState>withKey(record.key())
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
            List<Long> timestampsToRemove = new ArrayList<>();
            while (iterator.hasNext()) {
                VersionedRecord<SpatMovementState> state = iterator.next();
                timestampsToRemove.add(state.timestamp());
                final SpatMovementState thisState = state.value();
                if (previousState != null) {
                    if (previousState.getPhaseState() != thisState.getPhaseState()) {
                        // Transition detected,
                        context().forward(record
                                    .withTimestamp(state.timestamp())
                                    .withValue(new SpatMovementStateTransition(previousState, thisState)));
                        // Remove the old spats from the buffer up to here
                        for (long timestamp : timestampsToRemove) {
                            stateStore.delete(record.key(), timestamp);
                        }
                        timestampsToRemove.clear();
                    }
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
