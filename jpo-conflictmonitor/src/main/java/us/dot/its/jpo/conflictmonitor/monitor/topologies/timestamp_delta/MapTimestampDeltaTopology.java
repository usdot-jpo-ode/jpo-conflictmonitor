package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.MapTimestampDeltaNotification;
import us.dot.its.jpo.conflictmonitor.monitor.processors.timestamp_deltas.BaseTimestampDeltaNotificationProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.processors.timestamp_deltas.MapTimestampDeltaNotificationProcessor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.utils.ProcessedMapUtils;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.TimestampDeltaConstants.DEFAULT_MAP_TIMESTAMP_DELTA_ALGORITHM;

@Component(DEFAULT_MAP_TIMESTAMP_DELTA_ALGORITHM)
@Slf4j
public class MapTimestampDeltaTopology
    extends BaseTimestampDeltaTopology<ProcessedMap<LineString>, MapTimestampDeltaParameters, MapTimestampDeltaEvent,
        MapTimestampDeltaNotification>
    implements MapTimestampDeltaStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    protected long extractMessageTimestamp(ProcessedMap<LineString> message) {
        return ProcessedMapUtils.getTimestamp(message);
    }

    @Override
    protected long extractOdeReceivedAt(ProcessedMap<LineString> message) {
        return ProcessedMapUtils.getOdeReceivedAt(message);
    }

    @Override
    protected MapTimestampDeltaEvent constructEvent() {
        return new MapTimestampDeltaEvent();
    }

    @Override
    protected Serde<MapTimestampDeltaEvent> eventSerde() {
        return JsonSerdes.MapTimestampDeltaEvent();
    }

    @Override
    protected Serde<MapTimestampDeltaNotification> notificationSerde() {
        return JsonSerdes.MapTimestampDeltaNotification();
    }

    @Override
    protected BaseTimestampDeltaNotificationProcessor<MapTimestampDeltaEvent, MapTimestampDeltaNotification> constructProcessor(Duration retentionTime, String eventStoreName, String keyStoreName) {
        return new MapTimestampDeltaNotificationProcessor(retentionTime, eventStoreName, keyStoreName);
    }

}
