package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
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
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.MapTimestampDeltaNotification;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MapTimestampDeltaNotificationProcessor
    extends ContextualProcessor<RsuIntersectionKey, MapTimestampDeltaEvent, RsuIntersectionKey, MapTimestampDeltaNotification> {

    final Duration retentionTime;
    final String eventStoreName;
    final String keyStoreName;

    VersionedKeyValueStore<RsuIntersectionKey, MapTimestampDeltaEvent> eventStore;

    // Store to keep track of all the keys.  Needed because Versioned state stores don't support range queries yet.
    KeyValueStore<RsuIntersectionKey, Boolean> keyStore;

    Cancellable punctuatorCancellationToken;

    public MapTimestampDeltaNotificationProcessor(final Duration retentionTime, final String eventStoreName,
                                                  final String keyStoreName) {
        this.retentionTime = retentionTime;
        this.eventStoreName = eventStoreName;
        this.keyStoreName = keyStoreName;
    }

    @Override
    public void init(ProcessorContext<RsuIntersectionKey, MapTimestampDeltaNotification> context) {
        try {
            super.init(context);
            eventStore = context.getStateStore(eventStoreName);
            keyStore = context.getStateStore(keyStoreName);
            punctuatorCancellationToken = context.schedule(retentionTime, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
        } catch (Exception e) {
            log.error("Error initializing MapTimestampDeltaNotificationProcessor");
        }
    }

    @Override
    public void process(Record<RsuIntersectionKey, MapTimestampDeltaEvent> record) {
        var key = record.key();
        var value = record.value();
        var timestamp = record.timestamp();
        // Ignore tombstones
        if (value == null) return;
        keyStore.put(key, true);
        eventStore.put(key, value, timestamp);
    }

    private void punctuate(final long timestamp) {
        final Instant toTime = Instant.now();
        final Instant fromTime = toTime.minus(retentionTime);

        // Check every intersection for notifications
        List<RsuIntersectionKey> keysToClean = new ArrayList<>();
        try (var iterator = keyStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<RsuIntersectionKey, Boolean> keyValue = iterator.next();
                RsuIntersectionKey key = keyValue.key;
                assessmentForIntersection(key, fromTime, toTime, timestamp);
                keysToClean.add(key);
             }
        } catch (Exception ex) {
            log.error("Error in MapTimestampDeltaNotificationProcessor.punctuate", ex);
        }

        // Clean up the store
        for (RsuIntersectionKey key : keysToClean) {
            keyStore.delete(key);
        }
    }

    // Read stored events for one intersection, calculate statistics, and emit notifications
    private void assessmentForIntersection(RsuIntersectionKey key, Instant fromTime, Instant toTime, long timestamp) {
        long numberOfEvents = 0;
        long minDeltaMillis = Integer.MIN_VALUE;
        long maxDeltaMillis = Integer.MAX_VALUE;
        long absTotalDeltaMillis = 0;

        var versionedQuery =
                MultiVersionedKeyQuery.<RsuIntersectionKey, MapTimestampDeltaEvent>withKey(key)
                        .fromTime(fromTime)
                        .withAscendingTimestamps();
        QueryResult<VersionedRecordIterator<MapTimestampDeltaEvent>> result =
                eventStore.query(versionedQuery, PositionBound.unbounded(), new QueryConfig(false));
        VersionedRecordIterator<MapTimestampDeltaEvent> resultIterator = result.getResult();

        while (resultIterator.hasNext()) {
            VersionedRecord<MapTimestampDeltaEvent> record = resultIterator.next();
            long recordTimestamp = record.timestamp();
            Instant recordInstant = Instant.ofEpochMilli(recordTimestamp);
            // Shouldn't happen but check timestamps, in case of stream-time vs clock time issue
            if (recordInstant.isBefore(fromTime) || recordInstant.isAfter(toTime)) {
                log.warn("Record instant {} is not between {} and {}, skipping it.", recordInstant, fromTime, toTime);
                continue;
            }
            ++numberOfEvents;
            MapTimestampDeltaEvent event = record.value();
            TimestampDelta delta = event.getDelta();
            long deltaMillis = delta.getDeltaMillis();
            if (deltaMillis < minDeltaMillis) minDeltaMillis = deltaMillis;
            if (deltaMillis > maxDeltaMillis) maxDeltaMillis = deltaMillis;
            absTotalDeltaMillis += delta.getAbsDeltaMillis();
        }

        if (numberOfEvents > 0) {
            MapTimestampDeltaNotification notification =
                    createNotification(key, fromTime, toTime, numberOfEvents, minDeltaMillis, maxDeltaMillis, absTotalDeltaMillis);
            context().forward(new Record<>(key, notification, timestamp));
        }



    }

    private MapTimestampDeltaNotification createNotification(final RsuIntersectionKey key, final Instant fromTime, final Instant toTime,
                                  final long numberOfEvents, final long minDeltaMillis, final long maxDeltaMillis,
                                  final long absTotalDeltaMillis) {
        final var notification = new MapTimestampDeltaNotification();
        final var timePeriod = new ProcessingTimePeriod();
        timePeriod.setBeginTimestamp(fromTime.toEpochMilli());
        timePeriod.setEndTimestamp(toTime.toEpochMilli());
        notification.setTimePeriod(timePeriod);
        notification.setIntersectionID(key.getIntersectionId());
        notification.setRoadRegulatorID(key.getRegion());
        notification.setNumberOfEvents(numberOfEvents);
        notification.setMinDeltaMillis(minDeltaMillis);
        notification.setMaxDeltaMillis(maxDeltaMillis);
        final double absMeanDeltaMillis = (double)absTotalDeltaMillis / (double)numberOfEvents;
        notification.setAbsMeanDeltaMillis(absMeanDeltaMillis);
        notification.setNotificationHeading(String.format("MAP Timestamp Delta Notification"));
        notification.setNotificationText("There were differences between the ODE ingest time and message timestamp.");
        notification.setKey(notification.getUniqueId());
        return notification;
    }
}
