package us.dot.its.jpo.conflictmonitor.monitor.topologies.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.RsuConfigKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import static org.apache.kafka.common.serialization.Serdes.String;
import static us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.*;

@Component
@Profile("!test")
public class ConfigTopology
    extends BaseStreamsTopology<ConfigParameters>
    implements ConfigStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(ConfigTopology.class);

    KafkaTemplate<String, String> kafkaTemplate;
    
    final Multimap<String, DefaultConfigListener> defaultListeners =
        Multimaps.synchronizedMultimap(ArrayListMultimap.create());
    
    final Multimap<String, IntersectionConfigListener> intersectionListeners =
        Multimaps.synchronizedMultimap(ArrayListMultimap.create());

    @Override
    public DefaultConfig<?> getDefaultConfig(String key) {
        if (streams != null) {
            var defaultStore = 
                streams.store(
                    StoreQueryParameters.fromNameAndType(parameters.getDefaultStateStore(), 
                    QueryableStoreTypes.<String, DefaultConfig<?>>keyValueStore()));
            return defaultStore.get(key);
        }
        logger.error("Streams is not initialized");
        return null;
    }

    @Override
    public <T> void updateDefaultConfig(DefaultConfig<T> value) {

        if (streams == null) {
            logger.error("Streams is not initialized.");
            return;
        }

        if (kafkaTemplate == null) {
            logger.error("KafkaTemplate is not initialized");
            return ;
        }

        logger.info("Writing default config to topic: {}", value);
        var mapper = DateJsonMapper.getInstance();
        String valueString = null;
        try {
            valueString = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send(parameters.getDefaultTopicName(), value.getKey(), valueString);

        // Call default listeners to update properties in spring components
        defaultListeners.get(value.getKey()).forEach(listener -> {
            logger.info("Executing listener for {}", value);
            listener.accept(value);
        });
    }

    @Override
    public <T> ConfigUpdateResult<T> updateCustomConfig(DefaultConfig<T> value) {
        ConfigUpdateResult<T> result = new ConfigUpdateResult<>();
        if (streams == null) {
            logger.error("Streams is not initialized.");
            result.setResult(ConfigUpdateResult.Result.ERROR);
            result.setMessage("Could not set config value.  Streams is not initialized.");
            return result;
        }

        if (kafkaTemplate == null) {
            logger.error("KafkaTemplate is not initialized");
            result.setResult(ConfigUpdateResult.Result.ERROR);
            result.setMessage("Could not set config value.  KafkaTemplate is not initialized.");
            return  result;
        }

        var defaultStore =
                streams.store(
                        StoreQueryParameters.fromNameAndType(parameters.getDefaultStateStore(),
                                QueryableStoreTypes.<String, DefaultConfig<?>>keyValueStore())
                );
        var oldValue = (T)defaultStore.get(value.getKey());
        result.<T>setOldValue(oldValue);
        logger.info("Writing custom config top topic: {}", value);
        var mapper = DateJsonMapper.getInstance();
        String valueString = null;
        try {
            valueString = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send(parameters.getCustomTopicName(), value.getKey(), valueString);

        // Call default listeners to update properties in spring components
        defaultListeners.get(value.getKey()).forEach(listener -> {
            logger.info("Executing listener for {}", value);
            listener.accept(value);
        });
        return result;
    }



    @Override
    public <T> ConfigUpdateResult<T> updateIntersectionConfig(IntersectionConfig<T> value, int intersectionId) {
        return null;
    }

    @Override
    public <T> ConfigUpdateResult<T> updateIntersectionConfig(IntersectionConfig<T> value, int intersectionId, int region) {
        return null;
    }

    @Override
    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Optional<IntersectionConfig<?>> getIntersectionConfig(String key, String rsuID) {
        if (streams != null) {
            var intersectionStore = 
                streams.store(
                    StoreQueryParameters.fromNameAndType(parameters.getIntersectionStateStore(),
                    QueryableStoreTypes.<RsuConfigKey, IntersectionConfig<?>>keyValueStore())  
                );
            
            try (var store = intersectionStore.all()) {
                while (store.hasNext()) {
                    KeyValue<RsuConfigKey, IntersectionConfig<?>> keyValue = store.next();
                    if (!Objects.equals(keyValue.key.getRsuId(), rsuID)) continue;
                    if (!Objects.equals(keyValue.key.getKey(), key)) continue;
                    return Optional.of(keyValue.value);
                }
            }
        }
        return Optional.<IntersectionConfig<?>>empty();
    }


    @Override
    public DefaultConfigMap mapDefaultConfigs() {
        var configs = new TreeMap<String, DefaultConfig<?>>();
        if (streams != null) {
            var defaultStore = 
                streams.store(
                    StoreQueryParameters.fromNameAndType(parameters.getDefaultStateStore(), 
                    QueryableStoreTypes.<String, DefaultConfig<?>>keyValueStore()));
            
            try (var store = defaultStore.all()) {
                while (store.hasNext()) {
                    var item = store.next();
                    configs.put(item.key, item.value);
                }
            }
        }
        return new DefaultConfigMap(configs);
    }



    

    @Override
    public Map<RsuConfigKey, IntersectionConfig<?>> mapIntersectionConfigs() {
        var configs = new TreeMap<RsuConfigKey, IntersectionConfig<?>>();
        if (streams != null) {
            var intersectionStore = 
                streams.store(
                    StoreQueryParameters.fromNameAndType(parameters.getIntersectionStateStore(),
                    QueryableStoreTypes.<RsuConfigKey, IntersectionConfig<?>>keyValueStore())  
                );
            
            try (var store = intersectionStore.all()) {
                while (store.hasNext()) {
                    var item = store.next();
                    if (configs.containsKey(item.key)) {
                        configs.put(item.key, item.value);
                    }
                    configs.put(item.key, item.value);
                }
            }
        }
        return configs;
    }




    @Override
    public Map<RsuConfigKey, IntersectionConfig<?>> mapIntersectionConfigs(String key) {
        var configs = new TreeMap<RsuConfigKey, IntersectionConfig<?>>();
        if (streams != null) {
            var intersectionStore = 
                streams.store(
                    StoreQueryParameters.fromNameAndType(parameters.getIntersectionStateStore(),
                    QueryableStoreTypes.<RsuConfigKey, IntersectionConfig<?>>keyValueStore())  
                );
            
            try (var store = intersectionStore.all()) {
                while (store.hasNext()) {
                    var item = store.next();
                    if (Objects.equals(item.key.getKey(), key)) {
                        configs.put(item.key, item.value);
                    }
                }
            }
        }
        return configs;
    }






    public void initializePropertiesAsync() {
        new Thread(this::initializeProperties).start();
    }

    @Override
    public void initializeProperties() {

        if (streams == null) {
            throw new RuntimeException("Cant initialize, streams is null");
        }

        // Wait until stream thread has started
        while (streams.state() != KafkaStreams.State.RUNNING) {
            logger.info("Waiting for streams to start running...");
            if (streams.state() == KafkaStreams.State.ERROR) {
                logger.error("Streams is in error state");
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.info("Interrupted while waiting for streams to start", e);
            }

        }




        for (var key : defaultListeners.keySet()) {
            var defaultConfig = getDefaultConfig(key);
            if (defaultConfig != null) {
                defaultListeners.get(key).forEach(listener -> listener.accept(defaultConfig));
                logger.info("Restored defaultConfig {}: {}", key, defaultConfig);
            }
        }
        for (var key : intersectionListeners.keySet()) {
            for (var intersectionConfig : mapIntersectionConfigs(key).values()) {
                intersectionListeners.get(key).forEach(listener -> listener.accept(intersectionConfig));
                logger.info("Restored intersectionConfig {}: {}", key, intersectionConfig);
            }
        }
        logger.info("Finished initializing config properties");
    }

    

    @Override
    public void registerDefaultListener(String key, DefaultConfigListener handler) {
        defaultListeners.put(key, handler);
    }

    @Override
    public void registerIntersectionListener(String key, IntersectionConfigListener handler) {
        intersectionListeners.put(key, handler);
    }



    @Override
    protected Logger getLogger() {
        return logger;
    }


    public Topology buildTopology() {
        
        
        var builder = new StreamsBuilder();

        final String defaultTopic = parameters.getDefaultTopicName();
        final String customTopic = parameters.getCustomTopicName();
        final String mergedTopic = parameters.getMergedTopicName();
        final String defaultStore = parameters.getDefaultStateStore();
        final String intersectionStore = parameters.getIntersectionStateStore();
        final String intersectionTopic = parameters.getIntersectionTableName();

        KTable<String, DefaultConfig<?>> defaultConfig = builder.table(defaultTopic,
                Consumed.with(String(), DefaultConfig()));

        KTable<String, DefaultConfig<?>> customConfig = builder.table(customTopic,
                Consumed.with(String(), DefaultConfig()));

        // Merge custom and default.  Select custom where it exists, default otherwise.
        KTable<String, DefaultConfig<?>> mergedConfig
                = defaultConfig.leftJoin(customConfig,
                (defaultValue, customValue) -> customValue != null ? customValue : defaultValue);

        mergedConfig.toStream().to(mergedTopic,
                Produced.with(String(), DefaultConfig()));

        // Create a materialized GlobalKTable for the merged default configuration
        builder.globalTable(mergedTopic,
                Consumed.with(
                        String(),
                        DefaultConfig()
                ),
                Materialized.<String, DefaultConfig<?>, KeyValueStore<Bytes, byte[]>>as(defaultStore)
                        .withKeySerde(String())
                        .withValueSerde(DefaultConfig()));



        // Create a materialized GlobalKTable for intersection-level configuration
        builder.globalTable(intersectionTopic,
            Consumed.with(
                IntersectionConfigKey(),
                IntersectionConfig()
            ),
            Materialized.<IntersectionConfigKey, IntersectionConfig<?>, KeyValueStore<Bytes, byte[]>>as(intersectionStore)
                .withKeySerde(IntersectionConfigKey())
                .withValueSerde(IntersectionConfig())
        );

        return builder.build();
        

    }

}
