package us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation;

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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class EventAggregationProcessor<TKey, TEvent extends Event, TAggEvent extends EventAggregation<TEvent>>
        extends ContextualProcessor<TKey, TEvent, TKey, TAggEvent> {

    // version timestamp is the end of the aggregation interval
    // Key contains all unique fields of the event
    VersionedKeyValueStore<TKey, TAggEvent> eventStore;

    // KevValueStore to keep track of all the keys the event store and earliest time period sent already
    // Needed because of the lack of range queries for Versioned KeyValueStores.
    KeyValueStore<TKey, Long> keyStore;

    Cancellable punctuatorCancellationToken;
    final String eventStoreName;
    final String keyStoreName;
    final AggregationParameters params;
    final Function<TEvent, TAggEvent> createAggEvent;
    final String aggEventName;

    /**
     *
     * @param eventStoreName Versioned event store name
     * @param keyStoreName Key store name
     * @param parameters Common aggregation parameters
     * @param createAggEvent A function that accepts an event of type TEvent and creates an aggregated event of type
     *                       TAggEvent.
     */
    public EventAggregationProcessor(String eventStoreName, String keyStoreName, AggregationParameters parameters,
                                     Function<TEvent, TAggEvent> createAggEvent, String aggEventName) {
        this.eventStoreName = eventStoreName;
        this.keyStoreName = keyStoreName;
        this.params = parameters;
        this.createAggEvent = createAggEvent;
        this.aggEventName = aggEventName;
    }

    @Override
    public void process(Record<TKey, TEvent> record) {
        final TKey key = record.key();
        final long recordTimestamp = record.timestamp();
        final ProcessingTimePeriod period = params.aggTimePeriod(recordTimestamp);
        final long periodEndTimestamp = period.getEndTimestamp();
        final TEvent event = record.value();
        if (params.isDebug()) log.info("process record: type: {}, key: {}, timestamp: {}, period ending: {}",
                event.getEventType(), key, recordTimestamp, periodEndTimestamp);

        // Keep track of keys
        keyStore.put(key, 0L);

        // Check if there is already an aggregated event for the time period
        VersionedRecord<TAggEvent> aggEventRecord = queryEventStore(key, periodEndTimestamp);

        if (aggEventRecord == null) {
            // Create and add new agg event
            final TAggEvent aggEvent = createAggEvent.apply(event);
            aggEvent.setTimePeriod(period);
            aggEvent.setNumberOfEvents(1);
            aggEvent.update(event);
            eventStore.put(key, aggEvent, periodEndTimestamp);
            if (params.isDebug()) log.info("process: Added new event to store: key: {}, aggEvent count: {}, periodEndTimestamp: {}",
                    key, aggEvent.getNumberOfEvents(), periodEndTimestamp);
        } else {
            // Increment and update the saved agg event
            final TAggEvent aggEvent = aggEventRecord.value();
            aggEvent.update(event);
            eventStore.put(key, aggEvent, periodEndTimestamp);
            if (params.isDebug()) log.debug("process: Updated existing event in store: key: {}, aggEvent count: {}, periodEndTimestamp: {}",
                    key, aggEvent.getNumberOfEvents(), periodEndTimestamp);
        }

    }

    private VersionedRecord<TAggEvent> queryEventStore(final TKey key, final long periodEndTimestamp) {
        // Get the record for the key and end-of-period timestamp.
        // A simple eventStore.get(key, periodEndTimestamp) does not work because it retrieves the latest
        // record before the given timestamp, not the specific record at that timestamp.
        //final var periodEndInstant = Instant.ofEpochMilli(periodEndTimestamp);
        var records = queryEventStore(key);
        // Filter because fromTime in the query doesn't behave quite as one would expect:
        // Ref. https://kafka.apache.org/39/javadoc/org/apache/kafka/streams/query/MultiVersionedKeyQuery.html#fromTime(java.time.Instant)
        var recordsAtTimestamp = records.stream().filter(record -> record.timestamp() == periodEndTimestamp).toList();
        if (recordsAtTimestamp.size() > 1) {
            log.error("There are {} records in the agg eventStore with the same key {} and timestamp {}, " +
                            "this shouldn't happen!",
                    recordsAtTimestamp.size(), key, periodEndTimestamp);
        }
        if (!recordsAtTimestamp.isEmpty()) {
            var record = recordsAtTimestamp.getFirst();
            if (params.isDebug()) log.debug("queryEventStore: found one item for key, timestamp: {}, {}", key, periodEndTimestamp);
            return record;
        } else {
            if (params.isDebug()) log.debug("queryEventStore: found no items for {} {}", key, periodEndTimestamp);
            return null;
        }
    }

    private List<VersionedRecord<TAggEvent>> queryEventStore(final TKey key) {
        // Get the record for the key and end-of-period timestamp.
        // A simple eventStore.get(key, periodEndTimestamp) does not work because it retrieves the latest
        // record before the given timestamp, not the specific record at that timestamp.
        //final var periodEndInstant = Instant.ofEpochMilli(periodEndTimestamp);
        var versionedQuery =
                MultiVersionedKeyQuery.<TKey, TAggEvent>withKey(key)
                        .fromTime(null)
                        .toTime(null);
        QueryResult<VersionedRecordIterator<TAggEvent>> queryResult =
                eventStore.query(versionedQuery, PositionBound.unbounded(), new QueryConfig(false));
        VersionedRecordIterator<TAggEvent> resultIterator = queryResult.getResult();
        var records = new ArrayList<VersionedRecord<TAggEvent>>();
        while (resultIterator.hasNext()) {
            VersionedRecord<TAggEvent> record = resultIterator.next();
            records.add(record);
        }
        if (params.isDebug()) log.debug("queryEventStore: Found {} records, timestamps: {}",
                records.size(),
                records.stream().map(VersionedRecord::timestamp).sorted().toArray());
        return records;
    }

    @Override
    public void init(ProcessorContext<TKey, TAggEvent> context) {
        try {
            super.init(context);
            eventStore = context.getStateStore(eventStoreName);
            keyStore = context.getStateStore(keyStoreName);
            final Duration punctuatorInterval = Duration.ofMillis(params.getCheckIntervalMs());
            punctuatorCancellationToken = context.schedule(punctuatorInterval, PunctuationType.WALL_CLOCK_TIME,
                    this::punctuate);
        } catch (Exception e) {
            log.error("Error initializing EventAggregationProcessor", e);
        }
    }

    private void punctuate(final long timestamp) {
        if (params.isDebug()) log.info("punctuate at {}, type: {}", timestamp, aggEventName);

        // Emit events for which the time period is elapsed.

        // Check for agg events earlier than the current period plus grace period for each known key
        List<TKey> keysToDelete = new ArrayList<>();

        try (var iterator = keyStore.all()) {
            while (iterator.hasNext()) {
                final KeyValue<TKey, Long> kv = iterator.next();
                final TKey key = kv.key;
                final long earliestSentAlready = kv.value;
                if (params.isDebug()) log.debug("punctuate: key: {}, earliest sent already: {}", key, earliestSentAlready);

                List<VersionedRecord<TAggEvent>> records = queryEventStore(key);

                // Check for elapsed time periods
                for (final VersionedRecord<TAggEvent> aggEventRecord : records) {

                    final long periodEndTimestamp = aggEventRecord.timestamp();
                    if (params.isDebug())
                        log.debug("punctuate: periodEndTimestamp: {}", periodEndTimestamp);

                    // Is the periodEndTimestamp of the time period longer ago than the grace period, but not earlier than the
                    // earliest already sent?
                    if (timestamp > periodEndTimestamp + params.getGracePeriodMs()
                        && periodEndTimestamp > earliestSentAlready) {

                        if (params.isDebug())
                            log.debug("punctuate: timestamp > periodEndTimestamp + gracePeriod: {} > {} and " +
                                            "periodEndTimestamp > earliestSentAlready: {} > {}",
                                    timestamp, periodEndTimestamp + params.getGracePeriodMs(),
                                    periodEndTimestamp, earliestSentAlready);

                        // Send agg event
                        TAggEvent aggEvent = aggEventRecord.value();

                        // Update eventGeneratedAt to current time
                        aggEvent.setEventGeneratedAt(timestamp);
                        context().forward(new Record<>(key, aggEvent, timestamp));

                        // Update earliestSentAlready
                        keyStore.put(key, periodEndTimestamp);
                        if (params.isDebug())
                            log.info("punctuate: Sent agg event: {}, {}, {}", key, aggEvent, timestamp);
                    }
                }
            }
        }
    }


}


