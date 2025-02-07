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
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmMessageCountProgressionEvent;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.BsmProperties;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class BsmMessageCountProgressionProcessor<Point> extends ContextualProcessor<BsmRsuIdKey, ProcessedBsm<Point>, BsmRsuIdKey, BsmMessageCountProgressionEvent> {

    private VersionedKeyValueStore<BsmRsuIdKey, ProcessedBsm<Point>> stateStore;
    private KeyValueStore<BsmRsuIdKey, ProcessedBsm<Point>> lastProcessedStateStore;
    private final BsmMessageCountProgressionParameters parameters;

    public BsmMessageCountProgressionProcessor(BsmMessageCountProgressionParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void init(ProcessorContext<BsmRsuIdKey, BsmMessageCountProgressionEvent> context) {
        super.init(context);
        stateStore = context.getStateStore(parameters.getProcessedBsmStateStoreName());
        lastProcessedStateStore = context.getStateStore(parameters.getLatestBsmStateStoreName());
    }

    @Override
    public void process(Record<BsmRsuIdKey, ProcessedBsm<Point>> record) {
        BsmRsuIdKey key = record.key();
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

        var query = MultiVersionedKeyQuery.<BsmRsuIdKey, ProcessedBsm<Point>>withKey(record.key())
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

                        int previousMessageCount = previousState.getProperties().getMsgCnt();
                        int currentMessageCount = thisState.getProperties().getMsgCnt();

                        if (previousHash == currentHash && previousMessageCount == currentMessageCount){
                            // No Change
                        }
                        else if (previousHash != currentHash && (previousMessageCount + 1) % 128 == currentMessageCount){ // changed with valid increment, including wrap-around from 127 to 0
                            // No Change
                        } 
                        else {
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

        BsmProperties props = bsmData.getProperties();
        ZonedDateTime timeStamp = props.getTimeStamp();
        String odeReceivedAt = props.getOdeReceivedAt();
        props.setTimeStamp(null);
        props.setOdeReceivedAt(null);
        

        int bsmMsgCount = props.getMsgCnt();
        props.setMsgCnt(0);

        int hash = bsmData.hashCode();

        // Restore Original Values
        props.setMsgCnt(bsmMsgCount);
        props.setTimeStamp(timeStamp);
        props.setOdeReceivedAt(odeReceivedAt);
    
        return hash;
    }

    private BsmMessageCountProgressionEvent createEvent(ProcessedBsm<Point> previousState, ProcessedBsm<Point> thisState) {
        BsmProperties previousProperties = previousState.getProperties();
        BsmProperties currentProperties = thisState.getProperties();

        BsmMessageCountProgressionEvent event = new BsmMessageCountProgressionEvent();
        event.setMessageType("BSM");
        event.setMessageCountA(previousProperties.getMsgCnt());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        event.setTimestampA(previousProperties.getTimeStamp().format(formatter));
        event.setMessageCountB(currentProperties.getMsgCnt());
        event.setTimestampB(currentProperties.getTimeStamp().format(formatter));
        if (currentProperties.getId() != null) {
            event.setVehicleId(currentProperties.getId());
        }
        return event;
    }

    @Override
    public void close() {
        super.close();
    }
}