package us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

public interface BaseTimestampDeltaStreamsAlgorithm<TMessage> {

    void buildTopology(StreamsBuilder builder, KStream<RsuIntersectionKey, TMessage> inputStream);

}
