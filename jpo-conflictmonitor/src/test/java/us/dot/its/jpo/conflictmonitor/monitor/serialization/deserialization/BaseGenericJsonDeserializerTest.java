package us.dot.its.jpo.conflictmonitor.monitor.serialization.deserialization;

import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseGenericJsonDeserializerTest {

    protected String configString;
    protected Object expectedValue;
    protected String expectedType;

    public BaseGenericJsonDeserializerTest(String configString, Object expectedValue, String expectedType) {
        this.configString = configString;
        this.expectedValue = expectedValue;
        this.expectedType = expectedType;
    }

    @Parameterized.Parameters
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
    public abstract void testDeserialize();





    final static String configTemplate = """
        {
          "key": "key",
          "category": "category",
          "value": %s,
          "type": "%s",
          "units": "SECONDS",
          "description": "description",
          "updateType": "INTERSECTION"
        }
        """;


    static String getConfig(Object value, String type) {
        return String.format(configTemplate, value, type);
    }



}
