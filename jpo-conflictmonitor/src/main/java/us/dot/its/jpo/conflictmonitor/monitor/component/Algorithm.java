package us.dot.its.jpo.conflictmonitor.monitor.component;

import org.apache.kafka.common.serialization.Serde;

/**
 * General interface for an algorithm that accepts control parameters
 * and starts running the algorithm.
 */
public interface Algorithm<TParameters> {

    /**
     * Set configuration parameters for the algorithm
     * 
     * @param parameters
     */
    void setParameters(TParameters parameters);

    /**
     * Initializes the algorithm
     */
    void initialize();

    /**
     * Starts running the algorithm to process messages
     */
    void start();
}
