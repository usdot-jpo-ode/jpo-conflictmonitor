package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import java.util.function.Consumer;

import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;

/**
 * Functional interface for a listener for intersection configuration updates.
 */
public interface IntersectionConfigListener extends Consumer<IntersectionConfig<?>> {
    
}
