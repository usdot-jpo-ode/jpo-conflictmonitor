package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import java.util.function.Consumer;

import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;

/**
 * Functional interface for a listener for default configuration updates
 * 
 */
public interface DefaultConfigListener extends Consumer<DefaultConfig<?>> {
    
}
