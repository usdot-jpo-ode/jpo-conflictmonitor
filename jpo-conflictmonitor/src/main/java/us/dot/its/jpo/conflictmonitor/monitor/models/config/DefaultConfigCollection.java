package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.Collection;

public class DefaultConfigCollection extends ConfigCollection<DefaultConfig<?>> {

    public DefaultConfigCollection() {
        super();
    }
    public DefaultConfigCollection(Collection<DefaultConfig<?>> configs) {
        super(configs);
    }
}
