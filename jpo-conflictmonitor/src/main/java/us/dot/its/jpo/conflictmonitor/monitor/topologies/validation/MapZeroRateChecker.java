package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ProcessingTimePeriod;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.time.Duration;


@Slf4j
public class MapZeroRateChecker
    extends BaseZeroRateChecker<ProcessedMap<LineString>, MapBroadcastRateEvent> {

    public MapZeroRateChecker(int rollingPeriodSeconds, int outputIntervalSeconds, String inputTopicName, String stateStoreName) {
        super(rollingPeriodSeconds, outputIntervalSeconds, inputTopicName, stateStoreName);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected MapBroadcastRateEvent createEvent() {
        return new MapBroadcastRateEvent();
    }




}
