package us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.VersionedKeyValueStore;
import org.apache.kafka.streams.state.WindowStore;

import java.time.Duration;

@Slf4j
public class EventAggregationProcessor<TKey, TEvent, TAggEvent> extends ContextualProcessor<TKey, TEvent, TKey, TAggEvent> {

    // version timestamp is the end of the aggregation interval
    // Key contains all unique fields of the event
    VersionedKeyValueStore<TKey, TAggEvent> store;

    Cancellable punctuatorCancellationToken;
    final String storeName;
    final Duration aggInterval;
    final long gracePeriodMs;
    final Duration punctuatorInterval;

    public EventAggregationProcessor(String storeName, Duration aggInterval, Duration punctuatorInterval, long gracePeriodMs) {
        this.storeName = storeName;
        this.aggInterval = aggInterval;
        this.punctuatorInterval = punctuatorInterval;
        this.gracePeriodMs = gracePeriodMs;
    }

    @Override
    public void process(Record<TKey, TEvent> record) {

    }

    @Override
    public void init(ProcessorContext<TKey, TAggEvent> context) {
        try {
            super.init(context);
            store = context.getStateStore(storeName);
            punctuatorCancellationToken = context.schedule(punctuatorInterval, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
        } catch (Exception e) {
            log.error("Error initializing EventAggregationProcessor", e);
        }
    }

    private void punctuate(final long timestamp) {

    }
}
