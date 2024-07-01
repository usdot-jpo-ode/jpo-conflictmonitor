package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@RunWith(Parameterized.class)
public class GenericJsonDeserializerTest extends BaseGenericJsonDeserializerTest {



    public GenericJsonDeserializerTest(String configString, Object expectedValue, String expectedType) {
        super(configString, expectedValue, expectedType);
    }


    // Test for method: public T deserialize(String topic, byte[] data)
    @Test
    @Override
    public void testDeserialize() {
        log.info("testDeserialize: {}", configString);
        try (GenericJsonDeserializer<DefaultConfig<?>> deserializer = new GenericJsonDeserializer<>(DefaultConfig.class)) {
            DefaultConfig<?> config = deserializer.deserialize("test", configString.getBytes());
            assertThat(config, notNullValue());
            assertThat("type", config.getType(), equalTo(expectedType));
            assertThat("value", config.getValue(), equalTo(expectedValue));
            assertThat("units", config.getUnits(), equalTo(UnitsEnum.SECONDS));
            assertThat("description", config.getDescription(), equalTo("description"));
            assertThat("category", config.getCategory(), equalTo("category"));
            assertThat("updateType", config.getUpdateType(), equalTo(UpdateType.INTERSECTION));
            assertThat("key", config.getKey(), equalTo("key"));
        }
    }


}
