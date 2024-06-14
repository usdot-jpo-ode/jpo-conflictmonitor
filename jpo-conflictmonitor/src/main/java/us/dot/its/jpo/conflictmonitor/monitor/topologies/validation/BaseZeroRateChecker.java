package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.BroadcastRateEvent;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.time.Duration;

/**
 * Base class for zero broadcast rate checker. Maintains a state store with the latest wall clock timestamp for each
 * key, and emits events if the current time is more than the rolling window period later.
 * @param <TItem> Type of input items, ProcessedMap or ProcessedSpat
 * @param <TEvent> Type of output events
 */
public abstract class BaseZeroRateChecker<TItem, TEvent extends BroadcastRateEvent>
    implements Processor<RsuIntersectionKey, TItem, RsuIntersectionKey, TEvent> {

    protected abstract Logger getLogger();

    protected abstract TEvent createEvent();

    private KeyValueStore<RsuIntersectionKey, Long> store;
    private final int maxAgeMillis;
    private final int checkEveryMillis;
    private final String inputTopicName;
    private final String stateStoreName;

    ProcessorContext<RsuIntersectionKey, TEvent> context;

    public BaseZeroRateChecker(final int rollingPeriodSeconds, final int outputIntervalSeconds, final String inputTopicName, final String stateStoreName) {
        this.maxAgeMillis = rollingPeriodSeconds * 1000;
        this.checkEveryMillis = outputIntervalSeconds * 1000;
        this.inputTopicName = inputTopicName;
        this.stateStoreName = stateStoreName;
    }

    @Override
    public void init(ProcessorContext<RsuIntersectionKey, TEvent> context) {
        this.context = context;
        this.store = context.getStateStore(stateStoreName);
        context.schedule(Duration.ofMillis(checkEveryMillis),
                PunctuationType.WALL_CLOCK_TIME,
                this::punctuate);
    }

    @Override
    public void process(Record<RsuIntersectionKey, TItem> record) {
        store.put(record.key(), System.currentTimeMillis());
    }

    private void punctuate(long timestamp) {
        // Check if any keys are older than the max age
        try (var storeIterator = store.all()) {
            while (storeIterator.hasNext()) {
                KeyValue<RsuIntersectionKey, Long> item =storeIterator.next();
                RsuIntersectionKey key = item.key;
                Long lastTimestamp = item.value;
                if (timestamp - lastTimestamp > maxAgeMillis) {
                    emitZeroEvent(key, timestamp);
                }
            }
        }
    }

    private void emitZeroEvent(RsuIntersectionKey key, long timestamp) {
        getLogger().info("emit zero rate event for key = {} at timestamp = {}", key, timestamp);
        TEvent event = createEvent();
        event.setSource(key.toString());
        event.setIntersectionID(key.getIntersectionId());
        event.setRoadRegulatorID(key.getRegion());
        event.setTopicName(inputTopicName);
        ProcessingTimePeriod timePeriod = new ProcessingTimePeriod();
        timePeriod.setBeginTimestamp(timestamp - maxAgeMillis);
        timePeriod.setEndTimestamp(timestamp);
        event.setTimePeriod(timePeriod);
        event.setNumberOfMessages(0);
        context.forward(new Record<>(key, event, timestamp));
    }
}
