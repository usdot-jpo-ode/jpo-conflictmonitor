package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.Map;

public class DefaultConfigMap extends BaseConfigMap<DefaultConfig<?>> {

    public DefaultConfigMap() { }

    public DefaultConfigMap(Map<String,? extends DefaultConfig<?>> configMap) {
        super(configMap);
    }
}
