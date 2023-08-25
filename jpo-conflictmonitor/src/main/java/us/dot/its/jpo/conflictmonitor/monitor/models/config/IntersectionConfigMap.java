package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.Map;

public class IntersectionConfigMap extends BaseConfigMap<IntersectionConfig<?>> {

    public IntersectionConfigMap() {}

    public IntersectionConfigMap(Map<String, IntersectionConfig<?>> configMap) {
        super(configMap);
    }
}
