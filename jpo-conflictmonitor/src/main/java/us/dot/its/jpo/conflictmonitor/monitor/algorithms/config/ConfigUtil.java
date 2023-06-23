package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;


import us.dot.its.jpo.conflictmonitor.monitor.models.config.ConfigMap;



public class ConfigUtil {
    
    /**
     * Get the intersection-specific value if available, otherwise return the default value.
     * @param <T>
     * @param rsuID
     * @param rsuMap
     * @param defaultValue
     * @return The intersection-specific or default value.
     */
    public static <T> T getIntersectionValue(String rsuID, ConfigMap<T> rsuMap, T defaultValue) {
        if (rsuMap != null && rsuMap.containsKey(rsuID)) {
            return rsuMap.get(rsuID);
        }
        return defaultValue;
    }
    
}
