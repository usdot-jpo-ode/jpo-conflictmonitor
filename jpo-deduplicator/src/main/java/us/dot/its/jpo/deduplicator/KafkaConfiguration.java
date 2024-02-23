package us.dot.its.jpo.deduplicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;
import org.apache.kafka.clients.producer.ProducerConfig;

@Component
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaConfiguration {

    @Autowired
    private DeduplicatorProperties properties;

    @Bean(name = "createKafkaTopics")
    public KafkaAdmin.NewTopics createKafkaTopics() {
        logger.info("createTopic");
        List<NewTopic> newTopics = new ArrayList<>();
        
        if(!properties.getConfluentCloudStatus()){
            try {
                for(var propEntry : admin.getConfigurationProperties().entrySet()) {
                    logger.info("KafkaAdmin property {} = {}", propEntry.getKey(), propEntry.getValue());
                }

                if (!autoCreateTopics) {
                    logger.info("Auto create topics is disabled");
                    return null;
                }

                logger.info("Creating topics: {}", createTopics);

                
                
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

                // Explicitly create the topics here to prevent error on first run
                admin.initialize();
                admin.createOrModifyTopics(newTopics.toArray(new NewTopic[0]));
                
                // Check that topics were created
                var topicDescMap = admin.describeTopics(topicNames.toArray(new String[0]));
                for (var entry : topicDescMap.entrySet()) {
                    String topicName = entry.getKey();
                    var desc = entry.getValue();
                    logger.info("Created topic {}: {}", topicName, desc);
                }


            } catch (Exception e) {
                logger.error("Exception in createKafkaTopics", e);
                throw e;
            }
        }

        // List out existing topics
        // Admin adminClient = Admin.create(properties.createStreamProperties("DeduplicatorAdminClient"));
        // ListTopicsOptions listTopicsOptions = new ListTopicsOptions().listInternal(true);
        // ListTopicsResult topicsResult = adminClient.listTopics(listTopicsOptions);
        // KafkaFuture<Set<String>> topicsFuture = topicsResult.names();
        // try {
        //     List<String> topicNames = new ArrayList<>();
        //     for(String topicName: topicsFuture.get()){
        //         logger.info("Found Topic: " + topicName);
        //         topicNames.add(topicName);
        //     }

        // } catch (InterruptedException e) {
        //     logger.error("Interruption Exception in createKafkaTopics. Unable to List existing Topics", e);
        // } catch (ExecutionException e) {
        //     logger.error("Execution Exception in createKafkaTopics. Unable to List existing Topics", e);
        // }


        return new NewTopics(newTopics.toArray(NewTopic[]::new));    
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Properties configProps = properties.createStreamProperties("Deduplicator-producer-factory");

        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<Object, Object> entry : configProps.entrySet()) {
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            map.put(key, value);
        }

        map.put(
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
          StringSerializer.class);
        map.put(
          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
          StringSerializer.class);

        return new DefaultKafkaProducerFactory<String, String>(map);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
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
        logger.info("kafka.topics.numPartitions = {}", numPartitions);
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
