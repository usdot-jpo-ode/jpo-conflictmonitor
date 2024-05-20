package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.time.Duration;


public class MapZeroRateChecker implements Processor<RsuIntersectionKey, ProcessedMap<LineString>, RsuIntersectionKey, MapBroadcastRateEvent> {

    private KeyValueStore<RsuIntersectionKey, Long> store;
    private final int maxAgeMillis;
    private final int checkEveryMillis;
    private final String inputTopicName;

    ProcessorContext<RsuIntersectionKey, MapBroadcastRateEvent> context;

    public MapZeroRateChecker(MapValidationParameters parameters) {
        this.maxAgeMillis = parameters.getRollingPeriodSeconds() * 1000;
        this.checkEveryMillis = parameters.getOutputIntervalSeconds() * 1000;
        this.inputTopicName = parameters.getInputTopicName();
    }

    @Override
    public void init(ProcessorContext<RsuIntersectionKey, MapBroadcastRateEvent> context) {
        this.context = context;
        this.store = context.getStateStore(MapValidationTopology.LATEST_TIMESTAMP_STORE);
        context.schedule(Duration.ofMillis(checkEveryMillis),
                PunctuationType.WALL_CLOCK_TIME,
                this::punctuate);
    }

    @Override
    public void process(Record<RsuIntersectionKey, ProcessedMap<LineString>> record) {
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
        var event = new MapBroadcastRateEvent();
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
