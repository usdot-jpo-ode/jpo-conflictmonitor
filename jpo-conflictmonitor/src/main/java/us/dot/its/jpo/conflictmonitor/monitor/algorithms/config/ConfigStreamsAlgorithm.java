package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ConfigurableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.StreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;

public interface ConfigStreamsAlgorithm
        extends ConfigAlgorithm, ConfigurableAlgorithm<ConfigParameters>, StreamsTopology {

    void setKafkaTemplate(KafkaTemplate<String, byte[]> kafkaTemplate);

}
