package us.dot.its.jpo.conflictmonitor.monitor.topologies.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.AlgorithmParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import static us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.*;

import java.lang.reflect.Field;

@Component
@Profile("!test")
public class ConfigInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ConfigInitializer.class);



    final ConfigParameters configParams;

    final AlgorithmParameters algorithmParameters;

    final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public ConfigInitializer(
            ConfigParameters configParams,
            AlgorithmParameters algorithmParameters,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.configParams = configParams;
        this.algorithmParameters = algorithmParameters;
        this.kafkaTemplate = kafkaTemplate;
    }




    /**
     * Initialize the database with parameters fields annotated with {@link ConfigData}.
     */
    public void initializeDefaultConfigs() {
        logger.info("Initializing default configs");
        if (algorithmParameters == null) {
            logger.error("No algorithm parameters found");
            return;
        }
        for (Object paramObj : algorithmParameters.listParameterObjects()) {
            writeDefaultConfigObject(paramObj);
        }
    }

    private void writeDefaultConfigObject(Object paramObj) {
        logger.info("Writing default config object: {}", paramObj.getClass().getName());
        Class<?> objClass = paramObj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(ConfigData.class)) continue;
            final String  fieldName = field.getName();
            final PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(paramObj);
            final var propValue = accessor.getPropertyValue(fieldName);
            var updatable = field.getAnnotation(ConfigData.class);
            final Class<?> type = field.getType();
            final DefaultConfig<?> config = createConfig(type, propValue, updatable);
            logger.info("config: {}", config);
            writeDefaultConfigToTopic(config);
        }
    }

    private DefaultConfig<?> createConfig(Class<?> type, Object propValue, ConfigData updatable) {

        if (Integer.class.equals(type) || "int".equals(type.getName())) {
            var config = new DefaultIntConfig();
            config.setValue((Integer)propValue);
            setConfigProps(config, updatable, Integer.class);
            return config;
        } else if (String.class.equals(type)) {
            var config = new DefaultStringConfig();
            config.setValue((String)propValue);
            setConfigProps(config, updatable, String.class);
            return config;
        } else if (Boolean.class.equals(type) || "boolean".equals(type.getName())) {
            var config = new DefaultBooleanConfig();
            config.setValue((Boolean)propValue);
            setConfigProps(config, updatable, Boolean.class);
            return config;
        } else if (Double.class.equals(type) || "double".equals(type.getName())) {
            var config = new DefaultDoubleConfig();
            config.setValue((Double)propValue);
            setConfigProps(config, updatable, Double.class);
            return config;
        } else if (Long.class.equals(type) || "long".equals(type.getName())) {
            var config = new DefaultLongConfig();
            config.setValue((Long)propValue);
            setConfigProps(config, updatable, Long.class);
            return config;
        } else {
            var config = new DefaultIntConfig();
            config.setValue((Integer)propValue);
            setConfigProps(config, updatable, Integer.class);
            return config;
        }
    }

    private void setConfigProps(DefaultConfig<?> config, ConfigData updatable, Class<?> actualType) {
        config.setCategory(updatable.category());
        config.setDescription(updatable.description());
        config.setKey(updatable.key());
        config.setUnits(updatable.units());
        config.setUpdateType(updatable.updateType());
        config.setType(actualType.getName());
    }

    private void writeDefaultConfigToTopic(DefaultConfig<?> config) {
        logger.info("Writing default config to topic. {}", config);
        final String topic = configParams.getDefaultTableName();
        var mapper = DateJsonMapper.getInstance();
        String configString = null;
        try {
            configString = mapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send(topic, config.getKey(), configString);

    }
}
