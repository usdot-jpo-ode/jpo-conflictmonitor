package us.dot.its.jpo.conflictmonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaConfiguration {

    @Bean(name = "createKafkaTopics")
    public KafkaAdmin.NewTopics createKafkaTopics() {
        logger.info("createTopic");

        if (!autoCreateTopics) {
            logger.info("Auto create topics is disabled");
            return null;
        }

        logger.info("Creating topics: {}", createTopics);

        
        List<NewTopic> newTopics = new ArrayList<>();
        List<String> topicNames = new ArrayList<>();
        for (var topic : createTopics) {
            
            // Get the name and config settings for the topic
            String topicName = (String)topic.getOrDefault("name", null);

            if (topicName == null) {
                logger.error("CreateTopic {} has no topic name", topic);
                break;
            }
            topicNames.add(topicName);

            Map<String, String> topicConfigs = new HashMap<>();
            String cleanupPolicy = (String)topic.getOrDefault("cleanupPolicy", null);
            if (cleanupPolicy != null) {
                topicConfigs.put("cleanup.policy", cleanupPolicy);
            }
            Integer retentionMs = (Integer)topic.getOrDefault("retentionMs", null);
            if (retentionMs != null) {
                topicConfigs.put("retention.ms", retentionMs.toString());
            }

            NewTopic newTopic = TopicBuilder
                .name(topicName)
                .partitions(numPartitions)
                .replicas(numReplicas)
                .configs(topicConfigs)
                .build();
            newTopics.add(newTopic);
            logger.info("New Topic: {}", newTopic);
        }


        // Verify if the topics exist
        try {
            var topicDescriptions = admin.describeTopics(topicNames.toArray(String[]::new));
            logger.info("Topics: {}", topicDescriptions);
        } catch (Exception uex) {
            logger.error("Could not create topic", uex);
        }


        return new NewTopics(newTopics.toArray(NewTopic[]::new));
        
        
    }

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfiguration.class);

    private boolean autoCreateTopics;
    private int numPartitions;
    private int numReplicas;
    private List<Map<String, Object>> createTopics;

    public boolean getAutoCreateTopics() {
        return autoCreateTopics;
    }

    public void setAutoCreateTopics(boolean autoCreateTopics) {
        this.autoCreateTopics = autoCreateTopics;
    }

    public int getNumPartitions() {
        return numPartitions;
    }

    public int getNumReplicas() {
        return numReplicas;
    }

    public List<Map<String, Object>> getCreateTopics() {
        return createTopics;
    }

    public void setNumPartitions(int numPartitions) {
        this.numPartitions = numPartitions;
    }

    public void setNumReplicas(int numReplicas) {
        this.numReplicas = numReplicas;
    }

    public void setCreateTopics(List<Map<String, Object>> createTopics) {
        this.createTopics = createTopics;
    }

    

    @Autowired
    private KafkaAdmin admin;



}
