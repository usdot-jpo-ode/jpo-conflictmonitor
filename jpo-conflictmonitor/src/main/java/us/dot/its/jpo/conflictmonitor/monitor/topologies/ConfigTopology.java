package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.common.serialization.Serdes.Void;
import static org.apache.kafka.streams.state.Stores.keyValueStoreBuilder;
import static org.apache.kafka.streams.state.Stores.persistentKeyValueStore;
import static us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.DefaultConfig;
import static us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.IntersectionConfig;
import static us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.RsuConfigKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
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
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;


import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.DefaultConfigListener;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.IntersectionConfigListener;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.RsuConfigKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIdPartitioner;

@Component
public class ConfigTopology
    implements ConfigStreamsAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(ConfigTopology.class);

    ConfigParameters parameters;
    Properties streamsProperties;
    Topology topology;
    KafkaStreams streams;
    StateListener stateListener;
    StreamsUncaughtExceptionHandler exceptionHandler;
    
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
    public Map<String, DefaultConfig<?>> mapDefaultConfigs() {
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
        return configs;
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
    public void initializeProperties() {
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
    public void start() {
        if (parameters == null) {
            throw new IllegalStateException("Start called before setting parameters.");
        }
        if (streamsProperties == null) {
            throw new IllegalStateException("Streams properties are not set.");
        }
        if (streams != null && streams.state().isRunningOrRebalancing()) {
            throw new IllegalStateException("Start called while streams is already running.");
        }
        logger.info("Starting ConfigTopology.");
        Topology topology = buildTopology();
        streams = new KafkaStreams(topology, streamsProperties);
        if (exceptionHandler != null) streams.setUncaughtExceptionHandler(exceptionHandler);
        if (stateListener != null) streams.setStateListener(stateListener);
        streams.start();
        logger.info("Started ConfigTopology.");
        
 
    }

    @Override
    public void stop() {
        logger.info("Stopping ConfigTopology.");
        if (streams != null) {
            streams.close();
            streams.cleanUp();
            streams = null;
        }
        logger.info("Stopped ConfigTopology.");
    }

    @Override
    public void setParameters(ConfigParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public ConfigParameters getParameters() {
        return parameters;
    }

    @Override
    public void setStreamsProperties(Properties streamsProperties) {
        this.streamsProperties = streamsProperties;
    }

    @Override
    public Properties getStreamsProperties() {
       return streamsProperties;
    }

    @Override
    public KafkaStreams getStreams() {
        return streams;
    }

    @Override
    public void registerStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    @Override
    public void registerUncaughtExceptionHandler(StreamsUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
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
