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

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_message_count_progression.SpatMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEvent;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class SpatMessageCountProgressionProcessor extends ContextualProcessor<RsuIntersectionKey, ProcessedSpat, RsuIntersectionKey, SpatMessageCountProgressionEvent> {

    private VersionedKeyValueStore<RsuIntersectionKey, ProcessedSpat> stateStore;
    private KeyValueStore<RsuIntersectionKey, ProcessedSpat> lastProcessedStateStore;
    private final SpatMessageCountProgressionParameters parameters;

    public SpatMessageCountProgressionProcessor(SpatMessageCountProgressionParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void init(ProcessorContext<RsuIntersectionKey, SpatMessageCountProgressionEvent> context) {
        super.init(context);
        stateStore = context.getStateStore(parameters.getProcessedSpatStateStoreName());
        lastProcessedStateStore = context.getStateStore(parameters.getLatestSpatStateStoreName());
    }

    @Override
    public void process(Record<RsuIntersectionKey, ProcessedSpat> record) {
        RsuIntersectionKey key = record.key();
        ProcessedSpat value = record.value();
        long timestamp = record.timestamp();
    
        // Insert new record into the buffer
        stateStore.put(key, value, timestamp);
    
        // Query the buffer, excluding the grace period relative to stream time "now".
        Instant excludeGracePeriod =
                Instant.ofEpochMilli(context().currentStreamTimeMs())
                        .minusMillis(parameters.getBufferGracePeriodMs());
    
        ProcessedSpat lastProcessedSpat = lastProcessedStateStore.get(key);
        Instant startTime;
        if (lastProcessedSpat != null) {
            startTime = Instant.ofEpochMilli(lastProcessedSpat.getUtcTimeStamp().toInstant().toEpochMilli());
        } else {
            // No transitions yet, base start time on time window
            startTime = Instant.ofEpochMilli(context().currentStreamTimeMs())
                    .minusMillis(parameters.getBufferTimeMs());
        }

        // Ensure excludeGracePeriod is not earlier than startTime
        if (excludeGracePeriod.isBefore(startTime)) {
            excludeGracePeriod = startTime;
        }
        
        var query = MultiVersionedKeyQuery.<RsuIntersectionKey, ProcessedSpat>withKey(record.key())
            .fromTime(startTime.minusMillis(1)) // Add a small buffer to include the exact startTime record
            .toTime(excludeGracePeriod)
            .withAscendingTimestamps();

        QueryResult<VersionedRecordIterator<ProcessedSpat>> result = stateStore.query(query,
                PositionBound.unbounded(),
                new QueryConfig(false));

        if (result.isSuccess()) {
            VersionedRecordIterator<ProcessedSpat> iterator = result.getResult();
            ProcessedSpat previousState = null;
            int recordCount = 0;

            while (iterator.hasNext()) {
                final VersionedRecord<ProcessedSpat> state = iterator.next();
                final ProcessedSpat thisState = state.value();
                recordCount++;

                // Skip records older than the last processed state
                if (lastProcessedSpat != null && thisState.getUtcTimeStamp().isBefore(lastProcessedSpat.getUtcTimeStamp())) {
                    continue;
                }

                if (previousState != null) {
                    long timeDifference = thisState.getUtcTimeStamp().toInstant().toEpochMilli() - previousState.getUtcTimeStamp().toInstant().toEpochMilli();

                    if (timeDifference < parameters.getBufferTimeMs()) {
                        int previousHash = calculateHash(previousState);
                        int currentHash = calculateHash(thisState);
                        int previousRevision = previousState.getRevision();
                        int currentRevision = thisState.getRevision();

                        if (previousHash == currentHash && previousRevision == currentRevision); // No change
                        else if (previousHash != currentHash && (previousRevision + 1) % 128 == currentRevision); // changed with valid increment, including wrap-around from 127 to 0
                        else {
                            SpatMessageCountProgressionEvent event = createEvent(previousState, thisState);
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
    
    private int calculateHash(ProcessedSpat spat) {

        ZonedDateTime utcTimeStamp = spat.getUtcTimeStamp();
        int revision = spat.getRevision();
        String odeReceivedAt = spat.getOdeReceivedAt();
        spat.setUtcTimeStamp(null);
        spat.setRevision(0);
        spat.setOdeReceivedAt(null);

        int hash = spat.hashCode();
        
        spat.setUtcTimeStamp(utcTimeStamp);
        spat.setRevision(revision);
        spat.setOdeReceivedAt(odeReceivedAt);

        return hash;
    }

    private SpatMessageCountProgressionEvent createEvent(ProcessedSpat previousState, ProcessedSpat thisState) {
        SpatMessageCountProgressionEvent event = new SpatMessageCountProgressionEvent();
        event.setMessageType("SPaT");
        event.setMessageCountA(previousState.getRevision());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        event.setTimestampA(previousState.getUtcTimeStamp().format(formatter));
        event.setMessageCountB(thisState.getRevision());
        event.setTimestampB(thisState.getUtcTimeStamp().format(formatter));
        if (thisState.getIntersectionId() != null) {
            event.setIntersectionID(thisState.getIntersectionId());
        } else {
            event.setIntersectionID(-1);
        }
        if (thisState.getRegion() != null) {
            event.setRoadRegulatorID(thisState.getRegion());
        } else {
            event.setRoadRegulatorID(-1);
        }
        event.setSource(thisState.getOriginIp());
        return event;
    }

    @Override
    public void close() {
        super.close();
    }
}