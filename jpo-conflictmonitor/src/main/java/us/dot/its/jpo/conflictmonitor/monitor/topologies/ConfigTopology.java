package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.BaseStreamsTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.RsuConfigKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.streams.state.Stores.keyValueStoreBuilder;
import static org.apache.kafka.streams.state.Stores.persistentKeyValueStore;
import static us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.*;

@Component
@Profile("!test")
public class ConfigTopology
    extends BaseStreamsTopology<ConfigParameters>
    implements ConfigStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(ConfigTopology.class);


    
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
        return null;
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


    @Override
    public <T> ConfigUpdateResult<T> updateDefaultConfig(String key, T value) {
        return null;
    }

    @Override
    public <T> ConfigUpdateResult<T> updateIntersectionConfig(int intersectionId, String key, T value) {
        return null;
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

        final String defaultTopicName = parameters.getDefaultTopicName();
        final String defaultStore = parameters.getDefaultStateStore();
        final String intersectionTopicName = parameters.getIntersectionTopicName();
        final String intersectionStore = parameters.getIntersectionStateStore();
        final String intersectionTableName = parameters.getIntersectionTableName();

        builder = builder
            // Create a Global Store for default config updates
            .addGlobalStore(
                keyValueStoreBuilder(
                    persistentKeyValueStore(defaultStore),
                    String(),
                    DefaultConfig()),
                defaultTopicName,
                Consumed.with(
                    String(), 
                    DefaultConfig()),
                () -> new GlobalStoreUpdater(defaultStore, defaultListeners)
            );

        // Create a materialized KTable for intersection-level config updates
        builder.stream(intersectionTopicName,
                Consumed.with(
                    String(),
                    IntersectionConfig()
                )
            )
            .peek((key, value) -> {
                logger.info("{}: {}", key, value);
            })
            .selectKey(
                (String oldKey, IntersectionConfig<?> value) 
                    -> new RsuConfigKey(value.getRsuID(), value.getKey())
            )
            // Trigger listeners
            .peek((key, value) -> {
                logger.info("{}: {}", key, value);
                logger.info("{}", intersectionListeners);
                if (value != null && key.getKey() != null && intersectionListeners.containsKey(key.getKey())) {
                    intersectionListeners.get(key.getKey()).forEach(intersection -> intersection.accept(value));
                }
            })
            .to(intersectionTableName,
                Produced.with(
                    RsuConfigKey(),
                    IntersectionConfig(),
                    new RsuIdPartitioner<RsuConfigKey, IntersectionConfig<?>>())
            );

        // Materialize for queries
        builder.table(intersectionTableName,
            Consumed.with(
                RsuConfigKey(), 
                IntersectionConfig()
            ),
            Materialized.<RsuConfigKey, IntersectionConfig<?>, KeyValueStore<Bytes, byte[]>>as(intersectionStore)
                .withKeySerde(RsuConfigKey())
                .withValueSerde(IntersectionConfig())
        );


            
            
        

        return builder.build();
        

    }

   
    // Ref. https://github.com/confluentinc/kafka-streams-examples/blob/7.1.1-post/src/main/java/io/confluent/examples/streams/GlobalStoresExample.java
    private static class GlobalStoreUpdater implements Processor<String, DefaultConfig<?>, Void, Void> {

        private final String storeName;
        private final Multimap<String, DefaultConfigListener> listeners;

        public GlobalStoreUpdater(final String storeName, Multimap<String, DefaultConfigListener> listeners) {
            this.storeName = storeName;
            this.listeners = listeners;
        }

        private KeyValueStore<String, DefaultConfig<?>> store;

        @Override
        public void init(final ProcessorContext<Void, Void> processorContext) {
            store = processorContext.getStateStore(storeName);
        }

        @Override
        public void process(final Record<String, DefaultConfig<?>> record) {
            store.put(record.key(), record.value());
            listeners.get(record.key()).forEach(listener -> listener.accept(record.value()));
        }

        @Override
        public void close() {
            // No-op
        }

    }


   
}
