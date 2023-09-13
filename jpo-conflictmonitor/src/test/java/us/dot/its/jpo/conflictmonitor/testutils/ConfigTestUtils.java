package us.dot.its.jpo.conflictmonitor.testutils;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUpdateResult;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;

import java.util.Properties;

public class ConfigTestUtils {

    public final static String defaultTableName = "topic.CmDefaultConfigTable";
    public final static String customTableName = "topic.CmCustomConfigTable";
    public final static String mergedTableName = "topic.CmMergedConfigTable";
    public final static String mergedStateStore = "default-config";
    public final static String intersectionStateStore = "intersection-config";
    public final static String intersectionTableName = "topic.CmIntersectionConfigTable";

    public final static String key = "spat.validation.lowerBound";
    public final static int defaultValue = 10;
    public final static int customValue = 9;
    public final static String category = "category";
    public final static UnitsEnum units = UnitsEnum.SECONDS;
    public final static String description = "description";
    public final static String customDescription = "customDescription";
    public final static String rsuId = "127.0.0.1";
    public final static int intersectionId = 111111;
    public final static int regionId = 1;
    public final static int intersectionValue = 12;

    public static ConfigParameters getParameters() {
        var parameters = new ConfigParameters();
        parameters.setDefaultTopicName(defaultTableName);
        parameters.setCustomTopicName(customTableName);
        parameters.setMergedTopicName(mergedTableName);
        parameters.setDefaultStateStore(mergedStateStore);
        parameters.setIntersectionStateStore(intersectionStateStore);
        parameters.setIntersectionTableName(intersectionTableName);
        return parameters;
    }

    public static Properties createStreamProperties(String name) {
        Properties streamProps = new Properties();
        streamProps.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        streamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:10093");
        streamProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        return streamProps;
    }

    public static DefaultConfig<Integer> getCustomConfig() {
        var defaultConfig = new DefaultConfig<Integer>();
        defaultConfig.setKey(key);
        defaultConfig.setValue(customValue);
        defaultConfig.setCategory(category);
        defaultConfig.setUnits(units);
        defaultConfig.setDescription(customDescription);
        defaultConfig.setType("java.lang.Integer");
        return defaultConfig;
    }

    public static DefaultConfig<?> getDefaultConfig() {
        var defaultConfig = new DefaultConfig<Integer>();
        defaultConfig.setKey(key);
        defaultConfig.setValue(defaultValue);
        defaultConfig.setCategory(category);
        defaultConfig.setUnits(units);
        defaultConfig.setDescription(description);
        defaultConfig.setType("java.lang.Integer");
        return defaultConfig;
    }

    public static <T> DefaultConfig<T> getDefaultConfig(T value, String type) {
        var defaultConfig = new DefaultConfig<T>();
        defaultConfig.setKey(key);
        defaultConfig.setValue(value);
        defaultConfig.setCategory(category);
        defaultConfig.setUnits(units);
        defaultConfig.setDescription(description);
        defaultConfig.setType(type);
        return defaultConfig;
    }



    public static IntersectionConfig<Integer> getIntersectionConfig() {
        var intersectionConfig = new IntersectionConfig<Integer>();
        intersectionConfig.setKey(key);
        intersectionConfig.setValue(intersectionValue);
        intersectionConfig.setCategory(category);
        intersectionConfig.setUnits(units);
        intersectionConfig.setDescription(description);
        intersectionConfig.setIntersectionID(intersectionId);
        intersectionConfig.setRoadRegulatorID(regionId);
        intersectionConfig.setType("java.lang.Integer");
        return intersectionConfig;
    }

    public static <T> IntersectionConfig<T> getIntersectionConfig(T value, String type) {
        var intersectionConfig = new IntersectionConfig<T>();
        intersectionConfig.setKey(key);
        intersectionConfig.setValue(value);
        intersectionConfig.setCategory(category);
        intersectionConfig.setUnits(units);
        intersectionConfig.setDescription(description);
        intersectionConfig.setIntersectionID(intersectionId);
        intersectionConfig.setRoadRegulatorID(regionId);
        intersectionConfig.setType(type);
        return intersectionConfig;
    }

    public static IntersectionConfig<Integer> getIntersectionConfig_NoRegion() {
        var intersectionConfig = getIntersectionConfig();
        intersectionConfig.setRoadRegulatorID(0);
        return intersectionConfig;
    }

    public static <T> IntersectionConfig<T> getIntersectionConfig_NoRegion(T value, String type) {
        var intersectionConfig = new IntersectionConfig<T>();
        intersectionConfig.setKey(key);
        intersectionConfig.setValue(value);
        intersectionConfig.setCategory(category);
        intersectionConfig.setUnits(units);
        intersectionConfig.setDescription(description);
        intersectionConfig.setIntersectionID(intersectionId);
        intersectionConfig.setRoadRegulatorID(0);
        intersectionConfig.setType(type);
        return intersectionConfig;
    }

    public static DefaultConfigMap getDefaultConfigMap() {
        var defaultConfigMap = new DefaultConfigMap();
        var defaultConfig = getDefaultConfig();
        defaultConfigMap.put(defaultConfig.getKey(), getDefaultConfig());
        return defaultConfigMap;
    }

    public static IntersectionConfigMap getIntersectionConfigMap() {
        var intersectionConfigMap = new IntersectionConfigMap();
        var intersectionConfig = getIntersectionConfig();
        intersectionConfigMap.putConfig(intersectionConfig);
        return intersectionConfigMap;
    }

    public static <T> ConfigUpdateResult<T> getUpdateResult(Config<T> config) {
        var result = new ConfigUpdateResult<T>();
        result.setNewValue(config);
        result.setResult(ConfigUpdateResult.Result.UPDATED);
        return result;
    }
}
