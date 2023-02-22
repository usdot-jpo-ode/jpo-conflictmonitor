package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;

/**
 * General interface for an algorithm implemented as a Kafka Streams
 * topology that takes streams parameters and exposes the underlying
 * and streams objects to do queries on and get diagnostics from.
 */
public interface StreamsTopology {
    
    void setStreamsProperties(Properties streamsProperties);
    Properties getStreamsProperties();

    
    KafkaStreams getStreams();

   /**
    * Register a callback method that receives notifications when the KafkaStreams state changes
    * @param stateListener
    */
    void registerStateListener(StateListener stateListener);

    /**
     * Register exception handler for exceptions thrown from streams processes.
     * @param exceptionHandler
     */
    void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler);

}
