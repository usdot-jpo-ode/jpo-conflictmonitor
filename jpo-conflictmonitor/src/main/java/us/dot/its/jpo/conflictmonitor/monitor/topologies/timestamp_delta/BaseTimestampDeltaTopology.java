package us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.BaseTimestampDeltaStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.ITimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.BaseTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.TimestampDelta;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.BaseTimestampDeltaNotification;
import us.dot.its.jpo.conflictmonitor.monitor.processors.timestamp_deltas.BaseTimestampDeltaNotificationProcessor;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import java.time.Duration;

public abstract class BaseTimestampDeltaTopology<TMessage, TParams extends ITimestampDeltaParameters,
        TEvent extends BaseTimestampDeltaEvent, TNotification extends BaseTimestampDeltaNotification>
    extends BaseStreamsBuilder<TParams>
    implements BaseTimestampDeltaStreamsAlgorithm<TMessage> {

    abstract protected Logger getLogger();
    abstract protected long extractMessageTimestamp(TMessage message);
    abstract protected long extractOdeReceivedAt(TMessage message);
    abstract protected TEvent constructEvent();
    abstract protected Serde<TEvent> eventSerde();
    abstract protected Serde<TNotification> notificationSerde();

    // Construct an instance of the notification processor
    abstract protected BaseTimestampDeltaNotificationProcessor<TEvent, TNotification>
        constructProcessor(Duration retentionTime, String eventStoreName, String keyStoreName);

    @Override
    public void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, TMessage> inputStream) {

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
                        eventSerde()
                );
        final var keyStoreBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(keyStoreName),
                        us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                        Serdes.Boolean()
                );
        builder.addStateStore(eventStoreBuilder);
        builder.addStateStore(keyStoreBuilder);

        KStream<RsuIntersectionKey, TEvent> eventStream =
                inputStream
                        // Ignore tombstones
                        .filter((rsuIntersectionKey, processedMap) -> processedMap != null)

                        // Calculate timestamp delta
                        .mapValues((rsuIntersectionKey, processedMap) -> {
                            TimestampDelta delta = new TimestampDelta();
                            delta.setMaxDeltaMillis(maxDeltaMilliseconds);
                            delta.setMessageTimestampMillis(extractMessageTimestamp(processedMap));
                            delta.setOdeIngestTimestampMillis(extractOdeReceivedAt(processedMap));
                            if (isDebug) {
                                getLogger().debug("RSU: {}, TimestampDelta: {}", rsuIntersectionKey.getRsuId(), delta);
                            }
                            return delta;
                        })

                        // Filter out deltas that shouldn't emit events
                        .filter((rsuIntersectionKey, timestampDelta) -> timestampDelta.emitEvent())

                        // Create Events
                        .mapValues((rsuIntersectionKey, timestampDelta) -> {
                            TEvent event = constructEvent();
                            event.setDelta(timestampDelta);
                            event.setSource(rsuIntersectionKey.getRsuId());
                            event.setIntersectionID(rsuIntersectionKey.getIntersectionId());
                            event.setRoadRegulatorID(rsuIntersectionKey.getRegion());
                            if (isDebug) {
                                getLogger().info("Producing TimestampDeltaEvent: {}", event);
                            }
                            return event;
                        });

        // Output events
        eventStream.to(outputTopicName, Produced.with(
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                eventSerde(),
                new IntersectionIdPartitioner<>()));    // Don't change partitioning of output



        // Collect events to issue hourly summary notifications
        eventStream
                .process(() -> constructProcessor(retentionTime, eventStoreName, keyStoreName),
                        eventStoreName, keyStoreName)
                .to(notificationTopicName,
                        Produced.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                notificationSerde(),
                                new IntersectionIdPartitioner<>()));
    }
}
