package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
public class GenericJsonStringDeserializerTest
    extends BaseGenericJsonDeserializerTest {
    public GenericJsonStringDeserializerTest(String configString, Object expectedValue, String expectedType) {
        super(configString, expectedValue, expectedType);
    }

    @Test
    @Override
    public void testDeserialize() {
        log.info("testDeserialize: {}", configString);
        GenericJsonStringDeserializer<DefaultConfig<?>> deserializer
                = new GenericJsonStringDeserializer<>(DefaultConfig.class);
        var mapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(DefaultConfig.class, deserializer);
        mapper.registerModule(module);
        DefaultConfig<?> config = null;
        try {
            config = mapper.readValue(configString, DefaultConfig.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
