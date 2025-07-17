package us.dot.its.jpo.conflictmonitor.monitor.algorithms.config;

import java.util.Collection;
import java.util.Optional;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.models.IntersectionRegion;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;

/**
 * Unit tests for {@link ConfigAlgorithm}.
 */
public class ConfigAlgorithmTest {

    
    
    @Test
    public void testRegisterConfigListeners() {
        var configAlgorithm = new ConfigAlgorithmImpl();
        var testParameters = new TestParameters();
        configAlgorithm.registerConfigListeners(testParameters);

        // Test that listeners were added
        assertThat(configAlgorithm.defaultListeners.keySet(), 
            containsInAnyOrder(TestParameters.DEFAULT_PARAM, TestParameters.INTERSECTION_PARAM));      
        assertThat(configAlgorithm.defaultListeners.keySet(), 
            not(contains(TestParameters.READ_ONLY_PARAM)));
        assertThat(configAlgorithm.intersectionListeners.keySet(), 
            contains(TestParameters.INTERSECTION_PARAM));
        assertThat(configAlgorithm.intersectionListeners.keySet(), 
            not(containsInAnyOrder(TestParameters.DEFAULT_PARAM, TestParameters.READ_ONLY_PARAM)));
        
        var defaultListeners = configAlgorithm.defaultListeners.get(TestParameters.DEFAULT_PARAM);
        assertThat(defaultListeners, hasSize(1));
        var defaultListener = defaultListeners.iterator().next();

        var defaultIntersectionListeners = configAlgorithm.defaultListeners.get(TestParameters.INTERSECTION_PARAM);
        assertThat(defaultIntersectionListeners, hasSize(1));
        var defaultIntersectionListener = defaultIntersectionListeners.iterator().next();

        var intersectionListeners = configAlgorithm.intersectionListeners.get(TestParameters.INTERSECTION_PARAM);
        assertThat(intersectionListeners, hasSize(1));
        var intersectionListener = intersectionListeners.iterator().next();
        
        
        // Test that default listener can update default value
        final String newDefaultValue = "newDefaultValue";
        var newDefaultConfig = new DefaultConfig<String>(TestParameters.DEFAULT_PARAM, "", newDefaultValue, 
            "", UnitsEnum.NONE, "");
        defaultListener.accept(newDefaultConfig);
        assertThat(testParameters.getDefaultParam(), equalTo(newDefaultValue));


        // Test that intersection listener can update an RSU-specific value independent of default value update
        //final String rsuId = "127.0.0.1";
        final String newIntersectionDefault = "newIntersectionDefaultValue";
        final String newIntersectionValue = "newIntersectionValue";
        var intDefaultConfig = new DefaultConfig<String>(TestParameters.INTERSECTION_PARAM, "", 
            newIntersectionDefault, "", UnitsEnum.NONE, "");
        defaultIntersectionListener.accept(intDefaultConfig);

        final int roadRegulatorId = 10;
        final int intersectionId = 100000;
        final var intersectionKey = new IntersectionRegion(intersectionId, roadRegulatorId);

        var newIntersectionConfig = new IntersectionConfig<String>(TestParameters.INTERSECTION_PARAM, 
            "", roadRegulatorId, intersectionId, newIntersectionValue, "", UnitsEnum.NONE, "");
        intersectionListener.accept(newIntersectionConfig);

        assertThat(testParameters.getIntersectionParam(), equalTo(newIntersectionDefault));

        assertThat(testParameters.getIntersectionParam(intersectionKey), equalTo(newIntersectionValue));

        // Test that null-valued intersection parameter is removed
        var deleteIntersectionConfig = new IntersectionConfig<Void>(TestParameters.INTERSECTION_PARAM, "", roadRegulatorId,
                intersectionId, null, "", UnitsEnum.NONE, "");
        intersectionListener.accept(deleteIntersectionConfig);

        assertThat(testParameters.getIntersectionParam(), equalTo(newIntersectionDefault));
        assertThat(testParameters.getIntersectionParam(intersectionKey), equalTo(newIntersectionDefault));
        
    }

    

    

    /**
     * Mock implementation of {@link ConfigAlgorithm} for testing.
     */
    public class ConfigAlgorithmImpl implements ConfigAlgorithm {

        @Override
        public void registerDefaultListener(String key, DefaultConfigListener handler) {
            defaultListeners.put(key, handler);
        }

        @Override
        public void registerIntersectionListener(String key, IntersectionConfigListener handler) {
            intersectionListeners.put(key, handler);
        }

        public final Multimap<String, DefaultConfigListener> defaultListeners =
            ArrayListMultimap.create();
    
        public final Multimap<String, IntersectionConfigListener> intersectionListeners =
            ArrayListMultimap.create();



        @Override
        public DefaultConfig<?> getDefaultConfig(String key) {
            return null;
        }

        @Override
        public DefaultConfigMap mapDefaultConfigs() {
            return null;
        }



        @Override
        public Optional<IntersectionConfig<?>> getIntersectionConfig(IntersectionConfigKey configKey) {
            return Optional.empty();
        }

        @Override
        public Collection<IntersectionConfig<?>> listIntersectionConfigs(String key) {
            return null;
        }


        @Override
        public IntersectionConfigMap mapIntersectionConfigs() {
            return null;
        }

        @Override
        public <T> void updateDefaultConfig(DefaultConfig<T> value) {

        }

        @Override
        public <T> ConfigUpdateResult<T> updateCustomConfig(DefaultConfig<T> value) {
            return null;
        }

        @Override
        public <T> ConfigUpdateResult<T> updateIntersectionConfig(IntersectionConfig<T> config) {
            return null;
        }

        @Override
        public ConfigUpdateResult<Void> deleteIntersectionConfig(IntersectionConfigKey configKey) throws ConfigException {
            return null;
        }


        @Override
        public void initializeProperties() {
        }

        @Override
        public void start() {
           
        }

        @Override
        public void stop() {
            
        }
 
    }

    @Getter
    @Setter
    public class TestParameters {

        public final static String READ_ONLY_PARAM = "readOnlyParam";
        public final static String DEFAULT_PARAM = "defaultParam";
        public final static String INTERSECTION_PARAM = "intersectionParam";

        @ConfigData(key = READ_ONLY_PARAM, updateType = UpdateType.READ_ONLY)
        public String readOnlyParam = READ_ONLY_PARAM;
        
        @ConfigData(key = DEFAULT_PARAM, updateType = UpdateType.DEFAULT)
        public String defaultParam = DEFAULT_PARAM;

        @ConfigData(key = INTERSECTION_PARAM, updateType = UpdateType.INTERSECTION)
       public String intersectionParam = INTERSECTION_PARAM;

        ConfigMap<String> intersectionParamMap = new ConfigMap<>();

        public String getIntersectionParam(IntersectionRegion intersectionKey) {
            return ConfigUtil.getIntersectionValue(intersectionKey, intersectionParamMap, intersectionParam);
        }
    }
}
