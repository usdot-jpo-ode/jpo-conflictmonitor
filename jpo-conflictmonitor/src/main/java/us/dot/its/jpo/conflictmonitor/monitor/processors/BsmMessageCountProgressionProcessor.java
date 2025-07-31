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

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_message_count_progression.BsmMessageCountProgressionParameters;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEvent;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuLogKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// TODO Use RsuLogKey
@Slf4j
public class BsmMessageCountProgressionProcessor<Point> extends ContextualProcessor<RsuLogKey, ProcessedBsm<Point>, RsuLogKey, BsmMessageCountProgressionEvent> {

    private VersionedKeyValueStore<RsuLogKey, ProcessedBsm<Point>> stateStore;
    private KeyValueStore<RsuLogKey, ProcessedBsm<Point>> lastProcessedStateStore;
    private final BsmMessageCountProgressionParameters parameters;

    public BsmMessageCountProgressionProcessor(BsmMessageCountProgressionParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void init(ProcessorContext<RsuLogKey, BsmMessageCountProgressionEvent> context) {
        super.init(context);
        stateStore = context.getStateStore(parameters.getProcessedBsmStateStoreName());
        lastProcessedStateStore = context.getStateStore(parameters.getLatestBsmStateStoreName());
    }

    @Override
    public void process(Record<RsuLogKey, ProcessedBsm<Point>> record) {
        RsuLogKey key = record.key();
        ProcessedBsm<Point> value = record.value();
        long timestamp = record.timestamp();

        // Insert new record into the buffer
        stateStore.put(key, value, timestamp);

        // Query the buffer, excluding the grace period relative to stream time "now".
        Instant excludeGracePeriod =
                Instant.ofEpochMilli(context().currentStreamTimeMs())
                        .minusMillis(parameters.getBufferGracePeriodMs());

        ProcessedBsm<Point> lastProcessedBsm = lastProcessedStateStore.get(key);
        Instant startTime;
        if (lastProcessedBsm != null) {
            startTime = lastProcessedBsm.getProperties().getTimeStamp().toInstant();
        } else {
            // No transitions yet, base start time on time window
            startTime = Instant.ofEpochMilli(context().currentStreamTimeMs())
                    .minusMillis(parameters.getBufferTimeMs());
        }

        // Ensure excludeGracePeriod is not earlier than startTime
        if (excludeGracePeriod.isBefore(startTime)) {
            excludeGracePeriod = startTime;
        }

        var query = MultiVersionedKeyQuery.<RsuLogKey, ProcessedBsm<Point>>withKey(record.key())
            .fromTime(startTime.minusMillis(1)) // Add a small buffer to include the exact startTime record
            .toTime(excludeGracePeriod)
            .withAscendingTimestamps();

        QueryResult<VersionedRecordIterator<ProcessedBsm<Point>>> result = stateStore.query(query,
                PositionBound.unbounded(),
                new QueryConfig(false));

        if (result.isSuccess()) {
            VersionedRecordIterator<ProcessedBsm<Point>> iterator = result.getResult();
            ProcessedBsm<Point> previousState = null;
            int recordCount = 0;

            while (iterator.hasNext()) {
                final VersionedRecord<ProcessedBsm<Point>> state = iterator.next();
                final ProcessedBsm<Point> thisState = state.value();
                recordCount++;

                // Skip records older than the last processed state
                if (lastProcessedBsm != null && thisState.getProperties().getTimeStamp().isBefore(lastProcessedBsm.getProperties().getTimeStamp())) {
                    continue;
                }

                if (previousState != null) {
                    long timeDifference = thisState.getProperties().getTimeStamp().toInstant().toEpochMilli() - previousState.getProperties().getTimeStamp().toInstant().toEpochMilli();

                    if (timeDifference < parameters.getBufferTimeMs()) {
                        int previousHash = calculateHash(previousState);
                        int currentHash = calculateHash(thisState);

                        BsmProperties previousProperties = previousState.getProperties();
                        BsmProperties currentProperties = thisState.getProperties();

                        int previousMessageCount = Math.toIntExact(previousProperties.getMsgCnt());
                        int currentMessageCount = Math.toIntExact(currentProperties.getMsgCnt());

                        if (previousHash == currentHash && previousMessageCount == currentMessageCount) {
                            // No change
                        } else if (previousHash != currentHash && (previousMessageCount + 1) % 128 == currentMessageCount) {
                            // changed with valid increment, including wrap-around from 127 to 0
                        } else {
                            BsmMessageCountProgressionEvent event = createEvent(previousState, thisState);
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

    private int calculateHash(ProcessedBsm<Point> bsmData) {
        final ZonedDateTime timeStamp = bsmData.getProperties().getTimeStamp();
        final String odeReceivedAt = bsmData.getProperties().getOdeReceivedAt();
        bsmData.getProperties().setTimeStamp(null);
        bsmData.getProperties().setOdeReceivedAt(null);

        // Save original msgCnt value and set them to 0
        final int originalMsgCnt = Math.toIntExact(bsmData.getProperties().getMsgCnt());
        bsmData.getProperties().setMsgCnt(0L);

        int hash = bsmData.hashCode();
    
        // Restore original msgCnt values
        bsmData.getProperties().setMsgCnt((long) originalMsgCnt);
        bsmData.getProperties().setTimeStamp(timeStamp);
        bsmData.getProperties().setOdeReceivedAt(odeReceivedAt);
        return hash;
    }

    private BsmMessageCountProgressionEvent createEvent(ProcessedBsm<Point> previousState, ProcessedBsm<Point> thisState) {
        BsmProperties previousProperties = previousState.getProperties();
        BsmProperties currentProperties = thisState.getProperties();

        BsmMessageCountProgressionEvent event = new BsmMessageCountProgressionEvent();
        event.setMessageType("BSM");
        event.setMessageCountA(Math.toIntExact(previousProperties.getMsgCnt()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        event.setTimestampA(previousState.getProperties().getTimeStamp().format(formatter));
        event.setMessageCountB(Math.toIntExact(currentProperties.getMsgCnt()));
        event.setTimestampB(thisState.getProperties().getTimeStamp().format(formatter));
        if (thisState.getProperties().getId() != null) {
            event.setVehicleId(thisState.getProperties().getId());
        }
        else if (thisState.getId() != null) {
            event.setVehicleId(thisState.getId().toString());
        }

        return event;
    }

    @Override
    public void close() {
        super.close();
    }
}