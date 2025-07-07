package us.dot.its.jpo.conflictmonitor.monitor.algorithms;


/**
 * General interface for an algorithm that can be configured with parameters, and started and stopped.
 * @param <TParameters> the type of parameters used to configure the algorithm
 */
public interface Algorithm<TParameters> extends ExecutableAlgorithm, ConfigurableAlgorithm<TParameters>  { }
