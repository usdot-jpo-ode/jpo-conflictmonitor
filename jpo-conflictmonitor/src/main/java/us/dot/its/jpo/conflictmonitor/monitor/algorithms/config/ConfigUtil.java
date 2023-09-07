package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;


import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfigKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionKey;


public class ConfigUtil {
    
    /**
     * Get the intersection-specific value if available, otherwise return the default value.
     * @param <T>
     * @param intersectionKey
     * @param configMap
     * @param defaultValue
     * @return The intersection-specific or default value.
     */
    public static <T> T getIntersectionValue(IntersectionKey intersectionKey, ConfigMap<T> configMap, T defaultValue) {
        if (configMap != null && configMap.containsKey(intersectionKey)) {
            return configMap.get(intersectionKey);
        }
        return defaultValue;
    }
    
}
