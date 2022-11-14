package us.dot.its.jpo.conflictmonitor.monitor;

import org.apache.kafka.common.serialization.Serde;

/**
 * General interface for an algorithm that accepts control parameters
 * and starts running the algorithm.
 */
public interface Algorithm<TParameters> {


    /**
     * Initializes the algorithm with parameters
     */
    void initialize(TParameters parameters);

    /**
     * Starts running the algorithm to process messages
     */
    void start();
}
