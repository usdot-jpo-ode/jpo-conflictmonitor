package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UpdateType;

@RunWith(Parameterized.class)
public class GenericJsonDeserializerTest {

    String configString;
    Object expectedValue;
    String expectedType;

    public GenericJsonDeserializerTest(String configString, Object expectedValue, String expectedType) {
        this.configString = configString;
        this.expectedValue = expectedValue;
        this.expectedType = expectedType;
    }

    @Parameters
    public static Collection<Object[]> getParams() {
        var params = new ArrayList<Object[]>();
        params.add(new Object[] { getConfig(10, "java.lang.Integer"), 10, "java.lang.Integer" });
        params.add(new Object[] { getConfig(10.5, "java.lang.Double"), 10.5, "java.lang.Double" });
        params.add(new Object[] { getConfig("\"test\"", "java.lang.String"), "test", "java.lang.String" });
        params.add(new Object[] { getConfig(true, "java.lang.Boolean"), true, "java.lang.Boolean" });
        params.add(new Object[] { getConfig(Long.MAX_VALUE, "java.lang.Long"), Long.MAX_VALUE, "java.lang.Long" });
        return params;
    }

    // Test for method: public T deserialize(String topic, byte[] data)
    @Test
    public void testDeserialize() {
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

    

    final static String configTemplate = "{ " + //
                "  \"key\": \"key\", " + //
                "  \"category\": \"category\", " + //
                "  \"value\": %s, " + //
                "  \"type\": \"%s\", " + //
                "  \"units\": \"SECONDS\", " + //
                "  \"description\": \"description\", " + //
                "  \"updateType\": \"INTERSECTION\" " + //
                "}";

    
    static String getConfig(Object value, String type) {
        return String.format(configTemplate, value, type);
    }

    
    
}
