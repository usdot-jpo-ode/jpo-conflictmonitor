package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.function.BiConsumer;

/**
 * A Callback function for receiving old and new values of a config setting.
 */
public interface ConfigChange<T extends Config<?>> extends BiConsumer<T, T> {
    
}
