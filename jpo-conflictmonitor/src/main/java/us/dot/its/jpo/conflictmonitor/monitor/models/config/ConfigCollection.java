package us.dot.its.jpo.conflictmonitor.monitor.models.config;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigCollection<T extends Config<?>> extends ArrayList<T> {

    public ConfigCollection() { }

    public ConfigCollection(Collection<T> configs) {
        this.addAll(configs);
    }
    
}
