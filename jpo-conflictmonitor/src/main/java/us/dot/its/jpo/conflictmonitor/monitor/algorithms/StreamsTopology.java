package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;

/**
 * General interface for an algorithm implemented as a Kafka Streams
 * topology that takes streams parameters and exposes the underlying
 * and streams objects to do queries on and get diagnostics from.
 */
public interface StreamsTopology {
    
    void setStreamsProperties(Properties streamsProperties);
    Properties getStreamsProperties();

    
    KafkaStreams getStreams();

}
