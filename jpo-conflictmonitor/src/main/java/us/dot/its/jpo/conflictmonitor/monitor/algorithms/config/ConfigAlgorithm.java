package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ExecutableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;

/**
 * Service for monitoring updates to configuration parameters for other algorithms.
 */
public interface ConfigAlgorithm extends ExecutableAlgorithm {


    <T> DefaultConfig<T> getDefaultConfig(String key);
    <T> Optional<IntersectionConfig<T>> getIntersectionConfig(IntersectionConfigKey configKey);
    Collection<IntersectionConfig<?>> listIntersectionConfigs(String key);
    DefaultConfigMap mapDefaultConfigs();
    IntersectionConfigMap mapIntersectionConfigs();
    <T> void updateDefaultConfig(DefaultConfig<T> value);
    <T> ConfigUpdateResult<T> updateCustomConfig(DefaultConfig<T> value) throws ConfigException;
    <T> ConfigUpdateResult<T> updateIntersectionConfig(IntersectionConfig<T> value) throws ConfigException;

    void registerDefaultListener(String key, DefaultConfigListener handler);
    void registerIntersectionListener(String key, IntersectionConfigListener handler);

    /**
     * Initialize previously updated properties.
     * <p>Call this after all topologies have been initialized.
     */
    void initializeProperties();

    default void registerConfigListeners(final Object parameters) {
        registerDefaultListeners(parameters);
        registerIntersectionListeners(parameters);
    }

    default void registerDefaultListeners(final Object paramObj) {
        Class<?> objClass = paramObj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigData.class)) {
                var configDataAnnotation = field.getAnnotation(ConfigData.class);

                // Attach default listener if it isn't read-only
                if (!UpdateType.READ_ONLY.equals(configDataAnnotation.updateType())) {
                    final String  fieldName = field.getName();
                    final String key = configDataAnnotation.key();
                    final PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(paramObj);

                    registerDefaultListener(key, value ->  {
                        var logger = LoggerFactory.getLogger(ConfigAlgorithm.class);
                        logger.info("Default listener {}: {}", key, value);
                        final var propValue = value.getValue();
                        accessor.setPropertyValue(fieldName, propValue);
                    });
                }
            }
        }
    }

    default void registerIntersectionListeners(final Object paramObj) {
        Class<?> objClass = paramObj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigData.class)) {
                var configDataAnnotation = field.getAnnotation(ConfigData.class);

                // Attach intersection listener if it is intersection-updatable
                if (UpdateType.INTERSECTION.equals(configDataAnnotation.updateType())) {
                    // Attach event for the correspondingly named ConfigMap field.
                    final String mapFieldName = field.getName() + "Map";
                    final String key = configDataAnnotation.key();
                    final PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(paramObj);
                    try {
                        final ConfigMap<?> configMap = (ConfigMap<?>)accessor.getPropertyValue(mapFieldName);
                        registerIntersectionListener(key, value -> {
                            var logger = LoggerFactory.getLogger(ConfigAlgorithm.class);
                            logger.info("Intersection listener {}: {}", key, value);
                            final var propValue = value.getValue();
                            configMap.putObject(value.intersectionKey(), propValue);
                        });
                    } catch (InvalidPropertyException ex) {
                        var logger = LoggerFactory.getLogger(ConfigAlgorithm.class);
                        logger.error(String.format("The @ConfigData annotation on '%s' includes 'updateType=INTERSECTION', but the corresponding ConfigMap property named '%s' is missing", 
                            field.getName(), mapFieldName), ex);
                    }
                }
            }
        }
    }
}
