package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

/**
 * Generic type deserializer for Kafka
 * @param <T>
 */
public class GenericJsonDeserializer<T> implements Deserializer<T> {

    private static Logger logger = LoggerFactory.getLogger(GenericJsonDeserializer.class);

    protected final ObjectMapper mapper = DateJsonMapper.getInstance();

    private Class<?> genericClass;

    public GenericJsonDeserializer(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(String topic, byte[] data) {
       if (data == null) {
            return null;
       }
       try {
            JsonNode node = mapper.readValue(data, JsonNode.class);
            if (node.has("type")) {
                String type = node.get("type").asText();
                Class<?> nestedClass = Class.forName(type);
                JavaType javaType = mapper.getTypeFactory().constructParametricType(genericClass, nestedClass);
                return (T)mapper.readValue(data, javaType);
            }
            return null;
        } catch (IOException | ClassNotFoundException e) {
            String errMsg = String.format("Exception deserializing for topic %s: %s", topic, e.getMessage());
            logger.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        } 
    }
    
}
