package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.Map;
import java.util.TreeMap;

/**
 * Base class for a map of key to configuration settings.  Sorted by key.
 *
 * @param <T> - Type of configuration settings
 */
@Deprecated
public abstract class BaseConfigMap<T extends Config<?>> extends TreeMap<String, T> {

    public BaseConfigMap() { }

    public BaseConfigMap(Map<String, ? extends T> configMap) {
        this.putAll(configMap);
    }
}
