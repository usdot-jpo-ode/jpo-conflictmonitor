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




    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, ProcessedMap<LineString>> inputStream) {

        final String keyStoreName = parameters.getKeyStoreName();
        final String eventStoreName = parameters.getEventStoreName();
        final Duration retentionTime = Duration.ofMinutes(parameters.getRetentionTimeMinutes());
        final String outputTopicName = parameters.getOutputTopicName();
        final String notificationTopicName = parameters.getNotificationTopicName();
        final int maxDeltaMilliseconds = parameters.getMaxDeltaMilliseconds();
        final boolean isDebug = parameters.isDebug();

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
                    delta.setMaxDeltaMillis(maxDeltaMilliseconds);
                    delta.setMessageTimestampMillis(ProcessedMapUtils.getTimestamp(processedMap));
                    delta.setOdeIngestTimestampMillis(ProcessedMapUtils.getOdeReceivedAt(processedMap));
                    if (isDebug) {
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
                    if (isDebug) {
                        log.info("Producing TimestampDeltaEvent: {}", event);
                    }
                    return event;
                });

        // Output events
        eventStream.to(outputTopicName, Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                JsonSerdes.MapTimestampDeltaEvent(),
                new IntersectionIdPartitioner<>()));    // Don't change partitioning of output



        // Collect events to issue hourly summary notifications
        eventStream
                .process(() -> new MapTimestampDeltaNotificationProcessor(retentionTime, eventStoreName, keyStoreName),
                        eventStoreName, keyStoreName)
                .to(notificationTopicName,
                        Produced.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                JsonSerdes.MapTimestampDeltaNotification(),
                                new IntersectionIdPartitioner<>()));
    }
}
