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
    VersionedKeyValueStore<TKey, TAggEvent> store;

    Cancellable punctuatorCancellationToken;
    final String storeName;
    final Duration timeInterval;

    public EventAggregationProcessor(String storeName, Duration timeInterval) {
        this.storeName = storeName;
        this.timeInterval = timeInterval;

    }

    @Override
    public void process(Record<TKey, TEvent> record) {

    }

    @Override
    public void init(ProcessorContext<TKey, TAggEvent> context) {
        try {
            super.init(context);
            store = context.getStateStore(storeName);
            punctuatorCancellationToken = context.schedule(timeInterval, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
        } catch (Exception e) {
            log.error("Error initializing EventAggregationProcessor", e);
        }
    }

    private void punctuate(final long timestamp) {

    }
}
