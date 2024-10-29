package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;


/**
 * Streams implementation of an aggregation algorithm that plugs into a topology that produces an event.
 *
 * @param <TKey> The key type of the input event KStream
 * @param <TEvent> The event type of the input event KStream
 */
public interface BaseAggregationStreamsAlgorithm<TKey, TEvent extends Event> {

    void buildTopology(StreamsBuilder builder, KStream<TKey, TEvent> inputStream);

}
