package us.dot.its.jpo.conflictmonitor.monitor.processors.timestamp_deltas;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
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
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.BaseTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.BaseTimestampDeltaNotification;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract processor for generating timestamp delta notifications.
 * <p>
 * This processor collects timestamp delta events for each intersection, calculates statistics
 * over a retention window, and emits notifications summarizing the observed deltas.
 * <p>
 * Subclasses must implement methods to provide logger, notification construction, heading, and text.
 *
 * @param <TEvent>        the type of timestamp delta event processed
 * @param <TNotification> the type of notification emitted
 */
public abstract class BaseTimestampDeltaNotificationProcessor<TEvent extends BaseTimestampDeltaEvent,
        TNotification extends BaseTimestampDeltaNotification>
    extends ContextualProcessor<RsuIntersectionKey, TEvent, RsuIntersectionKey, TNotification> {

    /**
     * Returns the logger for this processor.
     *
     * @return logger instance
     */
    abstract protected Logger getLogger();

    /**
     * Constructs a new notification instance.
     *
     * @return a new notification object
     */
    abstract protected TNotification constructNotification();

    /**
     * Returns the heading text for the notification.
     *
     * @return notification heading
     */
    abstract protected String getNotificationHeading();

    /**
     * Returns the body text for the notification.
     *
     * @return notification text
     */
    abstract protected String getNotificationText();

    /** Retention time for events in the store. */
    final Duration retentionTime;
    /** Name of the event store. */
    final String eventStoreName;
    /** Name of the key store. */
    final String keyStoreName;

    /** Versioned key-value store for timestamp delta events. */
    VersionedKeyValueStore<RsuIntersectionKey, TEvent> eventStore;

    /**
     * Store to keep track of all the keys.
     * Needed because Versioned state stores don't support range queries yet.
     */
    KeyValueStore<RsuIntersectionKey, Boolean> keyStore;

    /** Cancellation token for the punctuator. */
    Cancellable punctuatorCancellationToken;

    /**
     * Constructs a BaseTimestampDeltaNotificationProcessor.
     *
     * @param retentionTime  duration to retain events for analysis
     * @param eventStoreName name of the event store
     * @param keyStoreName   name of the key store
     */
    public BaseTimestampDeltaNotificationProcessor(final Duration retentionTime, final String eventStoreName,
                                                  final String keyStoreName) {
        this.retentionTime = retentionTime;
        this.eventStoreName = eventStoreName;
        this.keyStoreName = keyStoreName;
    }

    /**
     * Initializes the processor, state stores, and schedules periodic punctuation.
     *
     * @param context the processor context
     */
    @Override
    public void init(ProcessorContext<RsuIntersectionKey, TNotification> context) {
        try {
            super.init(context);
            eventStore = context.getStateStore(eventStoreName);
            keyStore = context.getStateStore(keyStoreName);
            punctuatorCancellationToken = context.schedule(retentionTime, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
        } catch (Exception e) {
            getLogger().error("Error initializing MapTimestampDeltaNotificationProcessor", e);
        }
    }

    /**
     * Processes an incoming record, storing the event and updating the key store.
     * Ignores tombstone records (null values).
     *
     * @param record the record to process
     */
    @Override
    public void process(Record<RsuIntersectionKey, TEvent> record) {
        var key = record.key();
        var value = record.value();
        var timestamp = record.timestamp();
        // Ignore tombstones
        if (value == null) return;
        keyStore.put(key, true);
        eventStore.put(key, value, timestamp);
    }

    /**
     * Periodically called to process and emit notifications for all intersections.
     * Cleans up the key store after processing.
     *
     * @param timestamp the current wall-clock timestamp
     */
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
            getLogger().error("Error in punctuate method", ex);
        }

        // Clean up the store
        for (RsuIntersectionKey key : keysToClean) {
            keyStore.delete(key);
        }
    }

    /**
     * Reads stored events for one intersection, calculates statistics, and emits a notification.
     *
     * @param key       the intersection key
     * @param fromTime  start of the analysis window
     * @param toTime    end of the analysis window
     * @param timestamp the current wall-clock timestamp
     */
    private void assessmentForIntersection(RsuIntersectionKey key, Instant fromTime, Instant toTime, long timestamp) {
        var versionedQuery =
                MultiVersionedKeyQuery.<RsuIntersectionKey, TEvent>withKey(key)
                        .fromTime(fromTime)
                        .withAscendingTimestamps();
        QueryResult<VersionedRecordIterator<TEvent>> result =
                eventStore.query(versionedQuery, PositionBound.unbounded(), new QueryConfig(false));
        VersionedRecordIterator<TEvent> resultIterator = result.getResult();

        SummaryStatistics stats = new SummaryStatistics();
        DescriptiveStatistics absStats = new DescriptiveStatistics();
        while (resultIterator.hasNext()) {
            VersionedRecord<TEvent> record = resultIterator.next();
            long recordTimestamp = record.timestamp();
            Instant recordInstant = Instant.ofEpochMilli(recordTimestamp);
            // Shouldn't happen but check timestamps, in case of stream-time vs clock time issue
            if (recordInstant.isBefore(fromTime) || recordInstant.isAfter(toTime)) {
                getLogger().warn("Record instant {} is not between {} and {}, skipping it.", recordInstant, fromTime, toTime);
                continue;
            }
            TEvent event = record.value();
            TimestampDelta delta = event.getDelta();
            stats.addValue((double)delta.getDeltaMillis());
            absStats.addValue((double)delta.getAbsDeltaMillis());
        }

        long numberOfEvents = stats.getN();
        long minDeltaMillis = (long)stats.getMin();
        long maxDeltaMillis = (long)stats.getMax();
        double absMedianDelta = absStats.getPercentile(50.0);

        if (numberOfEvents > 0) {
            TNotification notification =
                    createNotification(key, fromTime, toTime, numberOfEvents, minDeltaMillis, maxDeltaMillis, absMedianDelta);
            context().forward(new Record<>(key, notification, timestamp));
        }
    }

    /**
     * Creates a notification object populated with statistics for the given intersection and time window.
     *
     * @param key             the intersection key
     * @param fromTime        start of the analysis window
     * @param toTime          end of the analysis window
     * @param numberOfEvents  number of events in the window
     * @param minDeltaMillis  minimum delta in milliseconds
     * @param maxDeltaMillis  maximum delta in milliseconds
     * @param absMedianDelta  median of absolute deltas
     * @return the constructed notification
     */
    private TNotification createNotification(final RsuIntersectionKey key, final Instant fromTime, final Instant toTime,
                                     final long numberOfEvents, final long minDeltaMillis, final long maxDeltaMillis,
                                     final double absMedianDelta) {
        final var notification = constructNotification();
        final var timePeriod = new ProcessingTimePeriod();
        timePeriod.setBeginTimestamp(fromTime.toEpochMilli());
        timePeriod.setEndTimestamp(toTime.toEpochMilli());
        notification.setTimePeriod(timePeriod);
        notification.setIntersectionID(key.getIntersectionId());
        notification.setRoadRegulatorID(key.getRegion());
        notification.setNumberOfEvents(numberOfEvents);
        notification.setMinDeltaMillis(minDeltaMillis);
        notification.setMaxDeltaMillis(maxDeltaMillis);
        notification.setAbsMedianDeltaMillis(absMedianDelta);
        notification.setNotificationHeading(getNotificationHeading());
        notification.setNotificationText(getNotificationText());
        notification.setKey(notification.getUniqueId());
        return notification;
    }
}
