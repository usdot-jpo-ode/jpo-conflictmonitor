package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;

@Slf4j
public class GenericJsonDeserializer_ConcurrentPermissiveTest
    extends BaseGenericJsonDeserializer_ConcurrentPermissiveTest {

    @Test
    @Override
    public void testDeserialize_ConcurrentPermissive() {
        log.info("testDeserialize: {}", configString);
        try (var deserializer = new GenericJsonDeserializer<IntersectionConfig<?>>(IntersectionConfig.class)) {
            IntersectionConfig<?> config = deserializer.deserialize("test", configString.getBytes());
            testConfigResult(config);
        }
    }


}
