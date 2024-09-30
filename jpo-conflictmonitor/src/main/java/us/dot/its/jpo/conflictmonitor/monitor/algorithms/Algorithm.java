package us.dot.its.jpo.conflictmonitor.monitor.algorithms;


/**
 * General interface for an algorithm that can be configured with parameters, and started and stopped.
 * 
 * @param <TParameters></TParameters> Type of configuration parameters the algorithm requires.
 */
public interface Algorithm<TParameters> extends ExecutableAlgorithm, ConfigurableAlgorithm<TParameters>  { }
