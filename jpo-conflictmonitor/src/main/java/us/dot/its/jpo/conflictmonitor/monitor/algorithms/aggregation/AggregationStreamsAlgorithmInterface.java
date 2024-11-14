package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.StreamPartitioner;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;


/**
 * Streams implementation of an aggregation algorithm that plugs into a topology that produces an event.
 *
 * @param <TKey> The key type of the input event KStream
 * @param <TEvent> The event type of the input event KStream
 * @param <TAggEvent> The type of the output aggregated event
 */
public interface AggregationStreamsAlgorithmInterface<TKey, TEvent extends Event, TAggEvent extends EventAggregation<TEvent>> {

    void buildTopology(StreamsBuilder builder, KStream<TKey, TEvent> inputStream);

    Class<TKey> keyClass();

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

    /**
     * Stream Partitioner for the output aggregated event.  Output events should use the same
     * partitioner as input events to minimize repartitioning in the topologies.
     *
     * @return An instance of the partitioner
     */
    StreamPartitioner<TKey, TAggEvent> eventAggregationPartitioner();

}
