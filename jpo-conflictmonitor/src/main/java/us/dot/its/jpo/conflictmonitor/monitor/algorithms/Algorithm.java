package us.dot.its.jpo.conflictmonitor.monitor.algorithms;


/**
 * General interface for an algorithm that can be configured with parameters, and started and stopped.
 * 
 */
public interface Algorithm<TParameters> extends ExecutableAlgorithm, ConfigurableAlgorithm<TParameters>  { }
