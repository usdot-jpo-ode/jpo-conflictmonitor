package us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.VersionedKeyValueStore;
import org.apache.kafka.streams.state.VersionedRecord;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class EventAggregationProcessor<TKey, TEvent, TAggEvent> extends ContextualProcessor<TKey, TEvent, TKey, TAggEvent> {

    // version timestamp is the end of the aggregation interval
    // Key contains all unique fields of the event
    VersionedKeyValueStore<TKey, TAggEvent> eventStore;
    KeyValueStore<TKey, Long> keyStore;

    Cancellable punctuatorCancellationToken;
    final String eventStoreName;
    final String keyStoreName;
    final AggregationParameters params;
    final Supplier<TAggEvent> aggEventSupplier;

    public EventAggregationProcessor(String eventStoreName, String keyStoreName, AggregationParameters parameters,
                                     Supplier<TAggEvent> aggEventSupplier) {
        this.eventStoreName = eventStoreName;
        this.keyStoreName = keyStoreName;
        this.params = parameters;
        this.aggEventSupplier = aggEventSupplier;
    }

    @Override
    public void process(Record<TKey, TEvent> record) {
        final TKey key = record.key();
        ProcessingTimePeriod period = params.aggTimePeriod(record.timestamp());
        final long endPeriodTimestamp = period.getEndTimestamp();
        // Check if there is already an aggregated event for the time period
        VersionedRecord<TAggEvent> aggEventRecord = eventStore.get(key, endPeriodTimestamp);
        if (aggEventRecord == null) {
            // Create and add new agg event
            TAggEvent aggEvent = aggEventSupplier.get();
            eventStore.put(key, aggEvent, endPeriodTimestamp);
        }

        final TEvent event = record.value();


    }

    @Override
    public void init(ProcessorContext<TKey, TAggEvent> context) {
        try {
            super.init(context);
            eventStore = context.getStateStore(eventStoreName);
            keyStore = context.getStateStore(keyStoreName);
            final var punctuatorInterval = Duration.ofMillis(params.getCheckIntervalMs());
            punctuatorCancellationToken = context.schedule(punctuatorInterval, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
        } catch (Exception e) {
            log.error("Error initializing EventAggregationProcessor", e);
        }
    }

    private void punctuate(final long timestamp) {

    }
}
