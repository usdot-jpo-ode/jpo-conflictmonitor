package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Thread safe map of a {@link IntersectionRegion} to a configuration value (String or Number).
 * 
 * <p>Keys are naturally ordered.
 * <p>Null values are not allowed by the underlying data structure.
 */
public class ConfigMap<T> extends ConcurrentSkipListMap<IntersectionRegion, T> {

    /**
     * A bit of a hack to allow setting either a String or numeric value 
     * from code where the generic type is not available.
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void putObject(IntersectionRegion key, Object value) {
        this.put(key, (T)value);
    }


}
