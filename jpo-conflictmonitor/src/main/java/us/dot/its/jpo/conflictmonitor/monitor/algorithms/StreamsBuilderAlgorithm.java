package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

import org.apache.kafka.streams.StreamsBuilder;

/**
 * General interface for an algorithm implemented as a StreamsBuilder that
 * builds part of a streams topology and plugs into a larger algorithm.
 */
public interface StreamsBuilderAlgorithm {
    StreamsBuilder buildTopology(StreamsBuilder builder);
}
