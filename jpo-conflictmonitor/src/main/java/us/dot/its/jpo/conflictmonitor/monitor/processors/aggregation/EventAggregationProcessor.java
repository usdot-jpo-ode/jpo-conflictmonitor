package us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.VersionedKeyValueStore;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;

import java.time.Duration;

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

    public EventAggregationProcessor(String eventStoreName, String keyStoreName, AggregationParameters parameters) {
        this.eventStoreName = eventStoreName;
        this.keyStoreName = keyStoreName;
        this.params = parameters;
    }

    @Override
    public void process(Record<TKey, TEvent> record) {
        final long timestamp = record.timestamp();
        ProcessingTimePeriod period = params.aggTimePeriod(timestamp);
        // TODO

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
