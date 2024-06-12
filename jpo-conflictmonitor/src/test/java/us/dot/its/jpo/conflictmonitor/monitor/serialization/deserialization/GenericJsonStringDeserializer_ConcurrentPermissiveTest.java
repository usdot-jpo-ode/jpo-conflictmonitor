package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;

@Slf4j
public class GenericJsonStringDeserializer_ConcurrentPermissiveTest
    extends BaseGenericJsonDeserializer_ConcurrentPermissiveTest {

    @Test
    @Override
    public void testDeserialize_ConcurrentPermissive() {
        log.info("testDeserialize: {}", configString);
        var deserializer = new GenericJsonStringDeserializer<IntersectionConfig<?>>(IntersectionConfig.class);
        var mapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(IntersectionConfig.class, deserializer);
        mapper.registerModule(module);
        IntersectionConfig<?> config = null;
        try {
            config = mapper.readValue(configString, IntersectionConfig.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        testConfigResult(config);
    }
}
