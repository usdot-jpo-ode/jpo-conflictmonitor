package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.Map;
import java.util.TreeMap;

public class DefaultConfigMap extends TreeMap<String, DefaultConfig<?>> {

    public DefaultConfigMap() { }

    public DefaultConfigMap(Map<String,? extends DefaultConfig<?>> configMap) {
        super(configMap);
    }
}
