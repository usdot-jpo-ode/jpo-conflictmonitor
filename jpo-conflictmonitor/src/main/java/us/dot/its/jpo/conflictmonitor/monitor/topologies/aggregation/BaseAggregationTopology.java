package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;


import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsBuilder;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationStreamsAlgorithmInterface;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;
import us.dot.its.jpo.conflictmonitor.monitor.processors.aggregation.EventAggregationProcessor;

import java.time.Duration;

public abstract class BaseAggregationTopology<TKey, TEvent extends Event, TAggEvent extends EventAggregation<TEvent>>
    extends
        BaseStreamsBuilder<AggregationParameters>
    implements
        AggregationAlgorithmInterface<TEvent, TAggEvent>,
        AggregationStreamsAlgorithmInterface<TKey, TEvent, TAggEvent> {

    @Override
    public KStream<TKey, TAggEvent> buildTopology(StreamsBuilder builder, KStream<TKey, TEvent> inputStream) {

        final String eventName = eventAggregationType();

        // Name stores by convention so we don't have to create properties for their names
        final String eventStoreName = eventName + "EventStore";
        final String keyStoreName = eventName + "KeyStore";

        final var eventTopicMap = parameters.getEventTopicMap();
        String eventAggregationTopic;
        if (eventTopicMap.containsKey(eventName)) {
            eventAggregationTopic = parameters.getEventTopicMap().get(eventName);
        } else {
            throw new RuntimeException(String.format("Aggregation topic for %s not found in aggregation.eventTopicMap",
                    eventName));
        }

        final long retentionTimeMillis = parameters.retentionTimeMs();
        getLogger().info("eventStore retention time = {} ms", retentionTimeMillis);

        final Duration retentionTime = Duration.ofMillis(retentionTimeMillis);

        final var eventStoreBuilder =
                Stores.versionedKeyValueStoreBuilder(
                        Stores.persistentVersionedKeyValueStore(eventStoreName, retentionTime),
                        keySerde(),
                        eventAggregationSerde()
                );
        final var keyStoreBuilder =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(keyStoreName),
                        keySerde(),
                        Serdes.Long()
                );
        builder.addStateStore(eventStoreBuilder);
        builder.addStateStore(keyStoreBuilder);

        var aggEventStream = inputStream
                .process(
                        () -> new EventAggregationProcessor<TKey, TEvent, TAggEvent>(
                                eventStoreName,
                                keyStoreName,
                                parameters,
                                this::constructEventAggregation,
                                eventName),
                        eventStoreName, keyStoreName);

        aggEventStream.to(eventAggregationTopic,
                        Produced.with(
                                keySerde(),
                                eventAggregationSerde(),
                                eventAggregationPartitioner()));

        // Return the KStream for topologies that produce notifications
        return aggEventStream;
    }



    @Override
    protected abstract Logger getLogger();

    @Override
    public abstract TAggEvent constructEventAggregation(TEvent event);

    @Override
    public abstract String eventAggregationType();

    @Override
    public abstract Class<TKey> keyClass();

    @Override
    public abstract Serde<TKey> keySerde();

    @Override
    public abstract Serde<TEvent> eventSerde();

    @Override
    public abstract Serde<TAggEvent> eventAggregationSerde();

    @Override
    public abstract StreamPartitioner<TKey, TAggEvent> eventAggregationPartitioner();
}
