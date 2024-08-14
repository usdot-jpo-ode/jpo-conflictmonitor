package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.utils.ProcessedMapUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import java.time.Duration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.TimestampDeltaConstants.DEFAULT_MAP_TIMESTAMP_DELTA_ALGORITHM;

@Component(DEFAULT_MAP_TIMESTAMP_DELTA_ALGORITHM)
@Slf4j
public class MapTimestampDeltaTopology
    extends BaseStreamsBuilder<MapTimestampDeltaParameters>
    implements MapTimestampDeltaStreamsAlgorithm {

    @Override
    protected Logger getLogger() {
        return log;
    }

    final String keyStoreName = "mapKeyStoreName";
    final String eventStoreName = "mapTimestampDeltaEvents";
    final Duration retentionTime = Duration.ofHours(1);
    final String notificationTopicName = "notificationTopicName";


    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, ProcessedMap<LineString>> inputStream) {

        final var eventStoreBuilder =
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(eventStoreName, retentionTime),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        JsonSerdes.MapTimestampDeltaEvent()
                );
        final var keyStoreBuilder =
                Stores.keyValueStoreBuilder(
                    Stores.persistentKeyValueStore(keyStoreName),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        Serdes.Boolean()
                );
        builder.addStateStore(eventStoreBuilder);
        builder.addStateStore(keyStoreBuilder);

        KStream<RsuIntersectionKey, MapTimestampDeltaEvent> eventStream =
            inputStream
                // Ignore tombstones
                .filter((rsuIntersectionKey, processedMap) -> processedMap != null)

                // Calculate timestamp delta
                .mapValues((rsuIntersectionKey, processedMap) -> {
                    TimestampDelta delta = new TimestampDelta();
                    delta.setMaxDeltaMillis(parameters.getMaxDeltaMilliseconds());
                    delta.setMessageTimestampMillis(ProcessedMapUtils.getTimestamp(processedMap));
                    delta.setOdeIngestTimestampMillis(ProcessedMapUtils.getOdeReceivedAt(processedMap));
                    if (parameters.isDebug()) {
                        log.debug("RSU: {}, TimestampDelta: {}", rsuIntersectionKey.getRsuId(), delta);
                    }
                    return delta;
                })

                // Filter out small deltas
                .filter((rsuIntersectionKey, timestampDelta) -> timestampDelta.emitEvent())

                // Create Event
                .mapValues((rsuIntersectionKey, timestampDelta) -> {
                    MapTimestampDeltaEvent event = new MapTimestampDeltaEvent();
                    event.setDelta(timestampDelta);
                    event.setSource(rsuIntersectionKey.getRsuId());
                    event.setIntersectionID(rsuIntersectionKey.getIntersectionId());
                    event.setRoadRegulatorID(rsuIntersectionKey.getRegion());
                    if (parameters.isDebug()) {
                        log.info("Producing TimestampDeltaEvent: {}", event);
                    }
                    return event;
                });

        // Output events
        eventStream.to(parameters.getOutputTopicName(), Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                JsonSerdes.MapTimestampDeltaEvent(),
                new IntersectionIdPartitioner<>()));    // Don't change partitioning of output



        // Collect events to issue hourly summary notifications
        eventStream
                .process(() -> new MapTimestampDeltaNotificationProcessor(retentionTime, eventStoreName), eventStoreName, keyStoreName)
                .to(notificationTopicName,
                        Produced.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                JsonSerdes.MapTimestampDeltaNotification(),
                                new IntersectionIdPartitioner<>()));
    }
}
