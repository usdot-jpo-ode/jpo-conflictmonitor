package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.Map;
import java.util.TreeMap;

public class IntersectionConfigMap extends TreeMap<IntersectionConfigKey, IntersectionConfig<?>> {

    public IntersectionConfigMap() {}

    public IntersectionConfigMap(Map<IntersectionConfigKey, IntersectionConfig<?>> configMap) {
        super(configMap);
    }
}
