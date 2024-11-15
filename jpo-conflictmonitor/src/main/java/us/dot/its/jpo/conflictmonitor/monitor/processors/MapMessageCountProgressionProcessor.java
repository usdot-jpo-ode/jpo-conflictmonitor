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

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.map_message_count_progression.MapMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.MapMessageCountProgressionEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.time.Instant;
import java.time.ZonedDateTime;

@Slf4j
public class MapMessageCountProgressionProcessor extends ContextualProcessor<String, ProcessedMap<LineString>, String, MapMessageCountProgressionEvent> {

    private VersionedKeyValueStore<String, ProcessedMap<LineString>> stateStore;
    private KeyValueStore<String, ProcessedMap<LineString>> lastProcessedStateStore;
    private final MapMessageCountProgressionParameters parameters;

    public MapMessageCountProgressionProcessor(MapMessageCountProgressionParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void init(ProcessorContext<String, MapMessageCountProgressionEvent> context) {
        super.init(context);
        stateStore = context.getStateStore(parameters.getProcessedMapStateStoreName());
        lastProcessedStateStore = context.getStateStore(parameters.getLatestMapStateStoreName());
    }

    @Override
    public void process(Record<String, ProcessedMap<LineString>> record) {
        String key = record.key();
        ProcessedMap<LineString> value = record.value();
        long timestamp = record.timestamp();

        // Insert new record into the buffer
        stateStore.put(key, value, timestamp);

        // Query the buffer, excluding the grace period relative to stream time "now".
        Instant excludeGracePeriod =
                Instant.ofEpochMilli(context().currentStreamTimeMs())
                        .minusMillis(parameters.getBufferGracePeriodMs());

        ProcessedMap<LineString> lastProcessedMap = lastProcessedStateStore.get(key);
        Instant startTime;
        if (lastProcessedMap != null) {
            startTime = lastProcessedMap.getProperties().getOdeReceivedAt().toInstant();
        } else {
            // No transitions yet, base start time on time window
            startTime = Instant.ofEpochMilli(context().currentStreamTimeMs())
                    .minusMillis(parameters.getBufferTimeMs());
        }

        // Ensure excludeGracePeriod is not earlier than startTime
        if (excludeGracePeriod.isBefore(startTime)) {
            excludeGracePeriod = startTime;
        }

        var query = MultiVersionedKeyQuery.<String, ProcessedMap<LineString>>withKey(record.key())
                .fromTime(startTime.minusMillis(1)) // Add a small buffer to include the exact startTime record
                .toTime(excludeGracePeriod)
                .withAscendingTimestamps();

        QueryResult<VersionedRecordIterator<ProcessedMap<LineString>>> result = stateStore.query(query,
                PositionBound.unbounded(),
                new QueryConfig(false));

        if (result.isSuccess()) {
            VersionedRecordIterator<ProcessedMap<LineString>> iterator = result.getResult();
            ProcessedMap<LineString> previousState = null;
            int recordCount = 0;

            while (iterator.hasNext()) {
                final VersionedRecord<ProcessedMap<LineString>> state = iterator.next();
                final ProcessedMap<LineString> thisState = state.value();
                recordCount++;

                // Skip records older than the last processed state
                if (lastProcessedMap != null && thisState.getProperties().getOdeReceivedAt().isBefore(lastProcessedMap.getProperties().getOdeReceivedAt())) {
                    continue;
                }

                if (previousState != null) {
                    long timeDifference = thisState.getProperties().getOdeReceivedAt().toInstant().toEpochMilli() - previousState.getProperties().getOdeReceivedAt().toInstant().toEpochMilli();

                    if (timeDifference < parameters.getBufferTimeMs()) {
                        int previousHash = calculateHash(previousState);
                        int currentHash = calculateHash(thisState);
                        int previousRevision = previousState.getProperties().getRevision();
                        int currentRevision = thisState.getProperties().getRevision();
                        int previousMsgIssueRevision = previousState.getProperties().getMsgIssueRevision();
                        int currentMsgIssueRevision = thisState.getProperties().getMsgIssueRevision();
                        boolean revisionChanged = previousHash != currentHash && (previousRevision + 1) % 128 != currentRevision;
                        boolean msgIssueRevisionChanged = previousHash != currentHash && (previousMsgIssueRevision + 1) % 128 != currentMsgIssueRevision;

                        if (previousHash == currentHash && previousRevision == currentRevision && previousMsgIssueRevision == currentMsgIssueRevision) {
                        } else if (revisionChanged || msgIssueRevisionChanged) {
                            MapMessageCountProgressionEvent event = createEvent(previousState, thisState, revisionChanged, msgIssueRevisionChanged);
                            context().forward(new Record<>(key, event, state.timestamp()));
                        }
                    }
                }
                previousState = thisState;
            }
            if (recordCount > 1) {
                // Update last processed state
                lastProcessedStateStore.put(key, previousState);
            }
        }
    }

    private int calculateHash(ProcessedMap<LineString> map) {
        ZonedDateTime utcTimeStamp = map.getProperties().getOdeReceivedAt();
        int revision = map.getProperties().getRevision();
        int msgIssueRevision = map.getProperties().getMsgIssueRevision();
        map.getProperties().setOdeReceivedAt(null);
        map.getProperties().setRevision(0);
        map.getProperties().setMsgIssueRevision(0);

        int hash = map.hashCode();

        map.getProperties().setOdeReceivedAt(utcTimeStamp);
        map.getProperties().setRevision(revision);
        map.getProperties().setMsgIssueRevision(msgIssueRevision);

        return hash;
    }

    private MapMessageCountProgressionEvent createEvent(ProcessedMap<LineString> previousState, ProcessedMap<LineString> thisState, boolean revisionChanged, boolean msgIssueRevisionChanged) {
        MapMessageCountProgressionEvent event = new MapMessageCountProgressionEvent();
        event.setMessageType("MAP");

        if (revisionChanged) {
            event.setMessageCountA(previousState.getProperties().getRevision());
            event.setMessageCountB(thisState.getProperties().getRevision());
        } else {
            event.setMessageCountA(previousState.getProperties().getMsgIssueRevision());
            event.setMessageCountB(thisState.getProperties().getMsgIssueRevision());
        }

        event.setTimestampA(previousState.getProperties().getOdeReceivedAt().toString());
        event.setTimestampB(thisState.getProperties().getOdeReceivedAt().toString());

        return event;
    }

    @Override
    public void close() {
        super.close();
    }
}