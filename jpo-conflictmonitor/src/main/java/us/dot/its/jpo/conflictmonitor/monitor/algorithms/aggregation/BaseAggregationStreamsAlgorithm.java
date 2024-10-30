package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;


/**
 * Streams implementation of an aggregation algorithm that plugs into a topology that produces an event.
 *
 * @param <TKey> The key type of the input event KStream
 * @param <TEvent> The event type of the input event KStream
 * @param <TAggEvent> The type of the output aggregated event
 */
public interface BaseAggregationStreamsAlgorithm<TKey, TEvent extends Event, TAggEvent extends Event> {

    void buildTopology(StreamsBuilder builder, KStream<TKey, TEvent> inputStream);

    /**
     * @return Serializer/deserializer for the key
     */
    Serde<TKey> keySerde();

    /**
     * @return Serializer/deserializer for the event class
     */
    Serde<TEvent> eventSerde();

    /**
     * @return Serializer/deserializer for the aggregated event class
     */
    Serde<TAggEvent> eventAggregationSerde();

}
