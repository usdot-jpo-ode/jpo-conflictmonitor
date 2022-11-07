package us.dot.its.jpo.conflictmonitor.monitor.processors;

import java.time.Duration;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.TimestampedKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

public class BsmEventProcessor extends AbstractProcessor<String, OdeBsmData> {

    private final String fStoreName = "bsm-event-state-store";
    private final Duration fPunctuationInterval = Duration.ofSeconds(10); // Check Every 10 Seconds
    private final long fSuppressTimeoutMillis = Duration.ofSeconds(10).toMillis(); // Emit event if no data for the last 10 seconds
    private TimestampedKeyValueStore<String, BsmEvent> stateStore;

    @Override
    public void init(ProcessorContext context) {
        super.init(context);
        stateStore = (TimestampedKeyValueStore<String, BsmEvent>) context.getStateStore(fStoreName);
        context.schedule(fPunctuationInterval, PunctuationType.WALL_CLOCK_TIME, this::punctuate);
    }

    @Override
    public void process(String key, OdeBsmData value) {

        // Key the BSM's based upon vehicle ID.
        key = key + "_" + ((J2735Bsm)value.getPayload().getData()).getCoreData().getId();
        ValueAndTimestamp<BsmEvent> record = stateStore.get(key);
        if (record != null) {

            BsmEvent event = record.value();
            event.setEndingBsm(value);
            stateStore.put(key, ValueAndTimestamp.make(event, context().timestamp()));
        } else {
            BsmEvent event = new BsmEvent(value);
            stateStore.put(key, ValueAndTimestamp.make(event, context().timestamp()));
        }
    }

    private void punctuate(long timestamp) {
        try (KeyValueIterator<String, ValueAndTimestamp<BsmEvent>> iterator = stateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, ValueAndTimestamp<BsmEvent>> record = iterator.next();
                if (context().timestamp() - record.value.timestamp() > fSuppressTimeoutMillis) {
                    context().forward(record.key, record.value.value());
                    stateStore.delete(record.key);
                }
            }
        }
    }
}