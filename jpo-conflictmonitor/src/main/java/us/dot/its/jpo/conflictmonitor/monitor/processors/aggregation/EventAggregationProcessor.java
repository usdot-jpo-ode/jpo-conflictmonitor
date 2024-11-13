package us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.VersionedKeyValueStore;
import org.apache.kafka.streams.state.VersionedRecord;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;

import java.time.Duration;
import java.util.function.Function;

@Slf4j
public class EventAggregationProcessor<TKey, TEvent extends Event, TAggEvent extends EventAggregation<TEvent>>
        extends ContextualProcessor<TKey, TEvent, TKey, TAggEvent> {

    // version timestamp is the end of the aggregation interval
    // Key contains all unique fields of the event
    VersionedKeyValueStore<TKey, TAggEvent> eventStore;

    // KevValueStore to keep track of all the keys and timestamps in the event store
    // Needed because of the lack of range queries for Versioned KeyValueStores.
    KeyValueStore<TKey, TimestampSet> keyStore;

    Cancellable punctuatorCancellationToken;
    final String eventStoreName;
    final String keyStoreName;
    final AggregationParameters params;
    final Function<TEvent, TAggEvent> createAggEvent;

    /**
     *
     * @param eventStoreName Versioned event store name
     * @param keyStoreName Key store name
     * @param parameters Common aggregation parameters
     * @param createAggEvent A function that accepts an event of type TEvent and creates an aggregated event of type
     *                       TAggEvent.
     */
    public EventAggregationProcessor(String eventStoreName, String keyStoreName, AggregationParameters parameters,
                                     Function<TEvent, TAggEvent> createAggEvent) {
        this.eventStoreName = eventStoreName;
        this.keyStoreName = keyStoreName;
        this.params = parameters;
        this.createAggEvent = createAggEvent;
    }

    @Override
    public void process(Record<TKey, TEvent> record) {
        final TKey key = record.key();
        final ProcessingTimePeriod period = params.aggTimePeriod(record.timestamp());
        final long periodEndTimestamp = period.getEndTimestamp();
        final TEvent event = record.value();

        // Keep track of time periods per key
        final TimestampSet endTimePeriods = keyStore.get(key);
        if (endTimePeriods == null || endTimePeriods.isEmpty()) {
            // This is the first received time period for this key, so add the current period
            final var timePeriods = new TimestampSet();
            timePeriods.add(periodEndTimestamp);
            keyStore.put(key, timePeriods);
        } else {
            // There are already timestamp periods for this key.
            // Discard the current event if it's period is earlier than the earliest known because the agg event was
            // already emitted.
            final Long earliestPeriodEndTimestamp = endTimePeriods.iterator().next();
            if (periodEndTimestamp < earliestPeriodEndTimestamp) {
                log.warn("""
                        An event, {}, arrived for aggregation period {}, which is earlier than the current aggregation
                        period ending at {}. It will be ignored. If there are a lot of these warnings, consider
                        adjusting the aggregation or grace period to get more accurate counts.
                        """, event, period, earliestPeriodEndTimestamp);
                return;
            }
            // Otherwise, it's a future or current period timestamp. Union with the active set of timestamps
            endTimePeriods.add(periodEndTimestamp);
        }

        // Check if there is already an aggregated event for the time period
        VersionedRecord<TAggEvent> aggEventRecord = eventStore.get(key, periodEndTimestamp);

        if (aggEventRecord == null) {
            // Create and add new agg event
            final TAggEvent aggEvent = createAggEvent.apply(event);
            aggEvent.setTimePeriod(period);
            aggEvent.setNumberOfEvents(1);
            aggEvent.update(event);
            eventStore.put(key, aggEvent, periodEndTimestamp);
        } else {
            // Increment and update the saved agg event
            final TAggEvent aggEvent = aggEventRecord.value();
            aggEvent.update(event);
            eventStore.put(key, aggEvent, periodEndTimestamp);
        }

    }

    @Override
    public void init(ProcessorContext<TKey, TAggEvent> context) {
        try {
            super.init(context);
            eventStore = context.getStateStore(eventStoreName);
            keyStore = context.getStateStore(keyStoreName);
            final Duration punctuatorInterval = Duration.ofMillis(params.getCheckIntervalMs());
            punctuatorCancellationToken = context.schedule(punctuatorInterval, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
        } catch (Exception e) {
            log.error("Error initializing EventAggregationProcessor", e);
        }
    }

    private void punctuate(final long timestamp) {
        // Emit events for which the time period is elapsed.


        // Check for agg events earlier than the current period plus grace period for each known key
        try (var iterator = keyStore.all()) {
            while (iterator.hasNext()) {
                final KeyValue<TKey, TimestampSet> kv = iterator.next();
                final TKey key = kv.key;
                final TimestampSet timePeriods = kv.value;
                final Long earliestPeriodEndTimeStamp = timePeriods.getFirst();
                if (timestamp > earliestPeriodEndTimeStamp + params.getGracePeriodMs()) {
                    VersionedRecord<TAggEvent> aggEventRecord = eventStore.get(key, earliestPeriodEndTimeStamp);

                    // Send agg event
                    if (aggEventRecord != null) {
                        TAggEvent aggEvent = aggEventRecord.value();
                        context().forward(new Record<>(key, aggEvent, timestamp));
                    }

                    // Remove the time period
                    timePeriods.remove(earliestPeriodEndTimeStamp);

                    // Delete the key if no time periods
                    if (timePeriods.isEmpty()) {
                        keyStore.delete(key);
                    } else {
                        keyStore.put(key, timePeriods);
                    }
                }
            }
        }
    }


}


