package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.Collection;

public class IntersectionConfigCollection extends ConfigCollection<IntersectionConfig<?>> {

    public IntersectionConfigCollection() {
        super();
    }

    public IntersectionConfigCollection(Collection<IntersectionConfig<?>> configs) {
        super(configs);
    }
}
