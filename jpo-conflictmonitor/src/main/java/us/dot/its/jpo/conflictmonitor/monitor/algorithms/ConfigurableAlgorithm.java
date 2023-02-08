package us.dot.its.jpo.conflictmonitor.monitor.algorithms;

/**
 * General interface for an algorithm that can be configured with parameters.
 * 
 * @param TParameters Type of configuration parameters the algorithm requires.
 */
public interface ConfigurableAlgorithm<TParameters> {

    /**
     * @param parameters The configuration parameters used by the algorithm.
     */
    void setParameters(TParameters parameters);
    TParameters getParameters();
    
}
