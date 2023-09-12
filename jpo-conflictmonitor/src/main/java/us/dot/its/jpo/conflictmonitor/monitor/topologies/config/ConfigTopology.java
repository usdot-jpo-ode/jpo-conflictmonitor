package us.dot.its.jpo.conflictmonitor.monitor.topologies.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import java.util.*;

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
    public <T> ConfigUpdateResult<T> updateCustomConfig(DefaultConfig<T> value) throws ConfigException {
        ConfigUpdateResult<T> result = new ConfigUpdateResult<>();
        try {

            // Perform Validations

            validateInitialized(result);

            // Verify that the configuration exists
            Config<?> oldConfig = getDefaultConfig(value.getKey());
            if (oldConfig == null) {
                result.setResult(ConfigUpdateResult.Result.ERROR);
                result.setMessage(String.format("An existing configuration with key %s was not found", value.getKey()));
                throw new ConfigException(result);
            }

            result.setOldValue((DefaultConfig<T>)oldConfig);
            result.setNewValue(value);

            // Don't allow updating read-only configs
            if (UpdateType.READ_ONLY.equals(oldConfig.getUpdateType())) {
                result.setResult(ConfigUpdateResult.Result.ERROR);
                result.setOldValue((DefaultConfig<T>) oldConfig.getValue());
                result.setMessage("The configuration is read-only and cannot be updated through the REST API");
                throw new ConfigException(result);
            }

            // Don't allow creating a new read-only config, or changing existing config to read-only
            if (UpdateType.READ_ONLY.equals(value.getUpdateType())) {
                result.setResult(ConfigUpdateResult.Result.ERROR);
                result.setMessage("Read-only configurations can't be created via the REST API.  Please add the configuration to the application config file.");
                throw new ConfigException(result);
            }


            // Don't allow changing type or units through the API
            validateTypeAndUnits(value, oldConfig, result);

            // Save the configuration to the topic
            logger.info("Writing custom config to kafka: {}", value);
            var mapper = DateJsonMapper.getInstance();
            String valueString = null;
            try {
                valueString = mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                result.setMessage(String.format("JsonProcessingException: %s", e.getMessage()));
                result.setResult(ConfigUpdateResult.Result.ERROR);
                throw new ConfigException(result, e);
            }
            kafkaTemplate.send(parameters.getCustomTopicName(), value.getKey(), valueString);


            // Call default listeners to update properties in Spring components

            defaultListeners.get(value.getKey()).forEach(listener -> {
                logger.info("Executing listener for {}", value);
                listener.accept(value);
            });

            result.setResult(ConfigUpdateResult.Result.UPDATED);
        } catch (ConfigException ce) {
            throw ce;
        } catch (Exception ex) {
            result.setResult(ConfigUpdateResult.Result.ERROR);
            result.setMessage(String.format("Exception setting DefaultConfig: %s", ex.getMessage()));
            logger.error(result.toString());
            throw new ConfigException(result);
        }

        return result;
    }

    private void validateInitialized(ConfigUpdateResult<?> result) throws ConfigException {
        if (streams == null) {
            logger.error("Streams is not initialized.");
            result.setResult(ConfigUpdateResult.Result.ERROR);
            result.setMessage("Could not set config value.  Streams is not initialized.");
            throw new ConfigException(result);
        }

        if (kafkaTemplate == null) {
            logger.error("KafkaTemplate is not initialized");
            result.setResult(ConfigUpdateResult.Result.ERROR);
            result.setMessage("Could not set config value.  KafkaTemplate is not initialized.");
            throw new ConfigException(result);
        }
    }

    private void validateTypeAndUnits(Config<?> value, Config<?> oldConfig, ConfigUpdateResult<?> result) throws ConfigException {
        if (!StringUtils.equals(value.getType(), oldConfig.getType())) {
            result.setMessage("The type property can't be changed through the REST API.");
            result.setResult(ConfigUpdateResult.Result.ERROR);
            throw new ConfigException(result);
        }
        if (!Objects.equals(value.getUnits(), oldConfig.getUnits())) {
            result.setMessage("The units property can't be changed through the REST API.");
            result.setResult(ConfigUpdateResult.Result.ERROR);
            throw new ConfigException(result);
        }
    }

    @Override
    public <T> ConfigUpdateResult<T> updateIntersectionConfig(IntersectionConfig<T> value) throws ConfigException {
        ConfigUpdateResult<T> result = new ConfigUpdateResult<>();
        try {
            // Perform validations
            validateInitialized(result);

            // Verify that the default configuration exists
            Config<?> defaultConfig = getDefaultConfig(value.getKey());
            if (defaultConfig == null) {
                result.setResult(ConfigUpdateResult.Result.ERROR);
                result.setMessage(String.format("A default configuration with key %s was not found", value.getKey()));
                throw new ConfigException(result);
            }

            // Don't allow intersection customizations unless Update Type is INTERSECTION
            if (!UpdateType.INTERSECTION.equals(defaultConfig.getUpdateType())) {
                result.setResult(ConfigUpdateResult.Result.ERROR);
                result.setMessage(String.format("The configuration %s is not customizable at the intersection level.", value.getKey()));
                throw new ConfigException(result);
            }

            // Don't allow changing type or units through the API
            validateTypeAndUnits(value, defaultConfig, result);

            // Retrieve old intersection config if it exists
            Optional<IntersectionConfig<?>> oldConfigOpt = getIntersectionConfig(value.intersectionKey());
            if (oldConfigOpt.isPresent()) {
                IntersectionConfig<?> oldConfig = oldConfigOpt.get();
                result.setOldValue((IntersectionConfig<T>)oldConfig);
            }
            result.setNewValue(value);

            logger.info("Writing intersection config to kafka: {}", value);
            var mapper = DateJsonMapper.getInstance();
            String keyString = null;
            String valueString = null;
            try {
                keyString = mapper.writeValueAsString(value.intersectionKey());
                valueString = mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                result.setMessage(String.format("JsonProcessingException: %s", e.getMessage()));
                result.setResult(ConfigUpdateResult.Result.ERROR);
                throw new ConfigException(result, e);
            }

            kafkaTemplate.send(parameters.getIntersectionTableName(), keyString, valueString);

            // Call intersection listeners to update properties in Spring components
            intersectionListeners.get(value.getKey()).forEach(listener -> {
                logger.info("Executing listener for {}", value);
                listener.accept(value);
            });

            result.setResult(ConfigUpdateResult.Result.UPDATED);

        } catch (ConfigException ce) {
            throw ce;
        } catch (Exception ex) {
            result.setResult(ConfigUpdateResult.Result.ERROR);
            result.setMessage(String.format("Exception setting IntersectionConfig: %s", ex.getMessage()));
            logger.error(result.toString());
            throw new ConfigException(result);
        }

        return result;
    }



    @Override
    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Optional<IntersectionConfig<?>> getIntersectionConfig(IntersectionConfigKey configKey) {
        if (configKey.getIntersectionID() <= 0) {
            // Special handling for unknown region
            return getIntersectionConfigUnknownRegion(configKey.getIntersectionID(), configKey.getKey());
        }
        if (streams != null) {
            var intersectionStore = getIntersectionStore();
            
            try (var store = intersectionStore.all()) {
                while (store.hasNext()) {
                    KeyValue<IntersectionConfigKey, IntersectionConfig<?>> keyValue = store.next();
                    if (!configKey.equals(keyValue.key)) continue;
                    return Optional.of(keyValue.value);
                }
            }
        } else {
            logger.error("Streams is not initialized");
        }
        return Optional.<IntersectionConfig<?>>empty();
    }

    @Override
    public Collection<IntersectionConfig<?>> listIntersectionConfigs(String key) {
        var configList = new ArrayList<IntersectionConfig<?>>();
        if (streams != null) {
            var intersectionStore = getIntersectionStore();

            try (var store = intersectionStore.all()) {
                while (store.hasNext()) {
                    KeyValue<IntersectionConfigKey, IntersectionConfig<?>> keyValue = store.next();
                    if (!StringUtils.equals(key, keyValue.key.getKey())) continue;
                    configList.add(keyValue.value);
                }
            }
        } else {
            logger.error("Streams is not initialized");
        }
        return configList;
    }


    private Optional<IntersectionConfig<?>> getIntersectionConfigUnknownRegion(int intersectionId, String key) {
        if (streams != null) {
            var intersectionStore = getIntersectionStore();

            try (var store = intersectionStore.all()) {
                List<IntersectionConfig<?>> configs = new ArrayList<>();
                while (store.hasNext()) {
                    KeyValue<IntersectionConfigKey, IntersectionConfig<?>> keyValue = store.next();
                    if (intersectionId != keyValue.key.getIntersectionID()) continue;
                    if (!StringUtils.equals(key, keyValue.key.getKey())) continue;
                    configs.add(keyValue.value);
                }
                if (configs.size() > 1) {
                    logger.error("More than one Config found in different regions for the same intersection id: {} and key: {}, returning the first one: {}",
                            intersectionId, key, configs);
                }
                if (configs.size() > 0) {
                    return Optional.of(configs.get(0));
                }
            }
        }
        return Optional.<IntersectionConfig<?>>empty();
    }



    private ReadOnlyKeyValueStore<IntersectionConfigKey, IntersectionConfig<?>> getIntersectionStore() {
        var intersectionStore =
                streams.store(
                        StoreQueryParameters.fromNameAndType(parameters.getIntersectionStateStore(),
                                QueryableStoreTypes.<IntersectionConfigKey, IntersectionConfig<?>>keyValueStore())
                );
        return intersectionStore;
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
    public IntersectionConfigMap mapIntersectionConfigs() {
        var configs = new IntersectionConfigMap();
        if (streams != null) {

            var intersectionStore = getIntersectionStore();

            try (var store = intersectionStore.all()) {
                while (store.hasNext()) {
                    var item = store.next();
                    configs.putConfig(item.value);
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
            for (var intersectionConfig : listIntersectionConfigs(key)) {
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
