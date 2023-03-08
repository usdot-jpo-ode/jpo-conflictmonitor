package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
@Component
@ConfigurationProperties(prefix = "config")
public class ConfigParameters {
    
    // Kafka topics
    String defaultTopicName;
    String intersectionTopicName;
    String defaultStateStore;
    String intersectionStateStore;
    String intersectionTableName;

    // Database collections
    String defaultCollectionName;
    String intersectionCollectionName;

    // Kafka connect paramters
    String createDefaultConnectorJsonParams;
    String createIntersectionConnectorJsonParams;
}
