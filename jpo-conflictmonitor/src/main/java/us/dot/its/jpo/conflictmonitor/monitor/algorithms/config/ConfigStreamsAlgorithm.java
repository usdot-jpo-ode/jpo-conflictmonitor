package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;

public interface ConfigStreamsAlgorithm
        extends ConfigAlgorithm, ConfigurableAlgorithm<ConfigParameters>, StreamsTopology {
}
