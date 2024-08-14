package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.StateStore;
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
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.MapTimestampDeltaNotification;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class MapTimestampDeltaNotificationProcessor
    extends ContextualProcessor<RsuIntersectionKey, MapTimestampDeltaEvent, RsuIntersectionKey, MapTimestampDeltaNotification> {

    final Duration retentionTime;
    final String eventStoreName;

    VersionedKeyValueStore<RsuIntersectionKey, MapTimestampDeltaEvent> eventStore;

    // Store to keep track of all the keys.  Needed because Versioned state stores don't support range queries yet.
    KeyValueStore<RsuIntersectionKey, Boolean> keyStore;

    Cancellable punctuatorCancellationToken;

    public MapTimestampDeltaNotificationProcessor(final Duration retentionTime, final String eventStoreName) {
        this.retentionTime = retentionTime;
        this.eventStoreName = eventStoreName;
    }

    @Override
    public void init(ProcessorContext<RsuIntersectionKey, MapTimestampDeltaNotification> context) {
        try {
            super.init(context);
            eventStore = context.getStateStore(eventStoreName);
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

    private void punctuate(long timestamp) {

        int numberOfEvents = 0;
        int minDeltaMillis = Integer.MIN_VALUE;
        int maxDeltaMillis = Integer.MAX_VALUE;

        final Instant fromTime = Instant.now().minus(retentionTime);
        try (var iterator = keyStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<RsuIntersectionKey, Boolean> keyValue = iterator.next();
                RsuIntersectionKey key = keyValue.key;
                var versionedQuery =
                        MultiVersionedKeyQuery.<RsuIntersectionKey, MapTimestampDeltaEvent>withKey(key)
                                .fromTime(fromTime)
                                .withAscendingTimestamps();

                QueryResult<VersionedRecordIterator<MapTimestampDeltaEvent>> result =
                    eventStore.query(versionedQuery, PositionBound.unbounded(), new QueryConfig(false));

                VersionedRecordIterator<MapTimestampDeltaEvent> resultIterator = result.getResult();
                while (resultIterator.hasNext()) {
                    VersionedRecord<MapTimestampDeltaEvent> record = resultIterator.next();
                    MapTimestampDeltaEvent event = record.value();

                }
            }
        } catch (Exception ex) {
            log.error("Error in MapTimestampDeltaNotificationProcessor.punctuate", ex);
        }
    }
}
