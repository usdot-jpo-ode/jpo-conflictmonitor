package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import java.io.IOException;

/**
 * Generic JSON deserializer for REST controllers, deserializes from string.
 */
@Slf4j
public class GenericJsonStringDeserializer<T> extends JsonDeserializer<T> {

    protected final ObjectMapper mapper = DateJsonMapper.getInstance();

    final Class<?> genericClass;

    public GenericJsonStringDeserializer(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {


        JsonNode node = mapper.readTree(jsonParser);
        if (node.has("type")) {
            String type = node.get("type").asText();
            Class<?> nestedClass = null;
            try {
                nestedClass = Class.forName(type);
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
            JavaType javaType = mapper.getTypeFactory().constructParametricType(genericClass, nestedClass);
            return (T)mapper.treeToValue(node, javaType);
        }
        return null;

    }
}
