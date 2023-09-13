package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.any;
import static us.dot.its.jpo.conflictmonitor.testutils.ConfigTestUtils.*;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;
import us.dot.its.jpo.conflictmonitor.testutils.ConfigTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTopologyTest {

    private static final Logger logger = LoggerFactory.getLogger(ConfigTopologyTest.class);



//    final String key = "key";
//    final int defaultValue = 10;
//    final int customValue = 9;
//    final String category = "category";
//    final UnitsEnum units = UnitsEnum.SECONDS;
//    final String description = "description";
//    final String customDescription = "customDescription";
//    final String rsuId = "127.0.0.1";
//    final int intersectionId = 11111;
//    final int regionId = 1;
//    final int intersectionValue = 12;


    @Test
    public void testConfigTopology() {

        
        var configTopology = new ConfigTopology();
        var configParams = ConfigTestUtils.getParameters();
        var streamsConfig = new Properties();
        configTopology.setParameters(configParams);


        var topology = configTopology.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {
            
            var defaultTopic = driver.createInputTopic(defaultTableName,
                    Serdes.String().serializer(),
                    JsonSerdes.DefaultConfig().serializer()
            );

            var customTopic = driver.createInputTopic(customTableName,
                    Serdes.String().serializer(),
                    JsonSerdes.DefaultConfig().serializer());

            var mergedTopic = driver.createOutputTopic(mergedTableName,
                    Serdes.String().deserializer(),
                    JsonSerdes.DefaultConfig().deserializer());

            var intersectionTopic = driver.createInputTopic(intersectionTableName,
                    JsonSerdes.IntersectionConfigKey().serializer(),
                    JsonSerdes.IntersectionConfig().serializer()
            );



            final var defaultConfig = getDefaultConfig();
            final var customConfig = getCustomConfig();
            final var intersectionConfig = getIntersectionConfig();

            defaultTopic.pipeInput(key, defaultConfig);
            var mergedStore = driver.getKeyValueStore(mergedStateStore);
            var defaultResult = mergedStore.get(key);
            
            assertThat("default value in merged state store", defaultResult, equalTo(defaultConfig));

            customTopic.pipeInput(key, customConfig);
            var customResult = mergedStore.get(key);
            assertThat("custom value in merged state store", customResult, equalTo(customConfig));


            intersectionTopic.pipeInput(intersectionConfig.intersectionKey(), intersectionConfig);
            var intersectionStore = driver.getKeyValueStore(intersectionStateStore);
            var intersectionResult = intersectionStore.get(new IntersectionConfigKey(regionId, intersectionId, key));
            assertThat("intersection state store", intersectionResult, equalTo(intersectionConfig));



        }

    }

    @Test
    public void testInitializeProperties() {
        var configTopology = new ConfigTopology();
        var streams = mock(KafkaStreams.class);
        when(streams.state()).thenReturn(KafkaStreams.State.ERROR, KafkaStreams.State.CREATED, KafkaStreams.State.RUNNING);
        configTopology.setStreams(streams);
        configTopology.initializeProperties();

    }

    @Mock ReadOnlyKeyValueStore<String, DefaultConfig<Integer>> defaultStore;

    @Test
    public void testGetDefaultConfig() {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        when(streams.store(any())).thenReturn(defaultStore);
        final DefaultConfig<Integer> defaultConfig = new DefaultConfig<>();
        when(defaultStore.get(anyString())).thenReturn(defaultConfig);
        configTopology.setStreams(streams);
        var result = configTopology.getDefaultConfig(key);
        assertThat(result, equalTo(defaultConfig));
    }




    
//    private DefaultConfig<Integer> getDefaultConfig() {
//        var defaultConfig = new DefaultConfig<Integer>();
//        defaultConfig.setKey(key);
//        defaultConfig.setValue(defaultValue);
//        defaultConfig.setCategory(category);
//        defaultConfig.setUnits(units);
//        defaultConfig.setDescription(description);
//        defaultConfig.setType("java.lang.Integer");
//        return defaultConfig;
//    }
//
//    private DefaultConfig<Integer> getCustomConfig() {
//        var defaultConfig = new DefaultConfig<Integer>();
//        defaultConfig.setKey(key);
//        defaultConfig.setValue(customValue);
//        defaultConfig.setCategory(category);
//        defaultConfig.setUnits(units);
//        defaultConfig.setDescription(customDescription);
//        defaultConfig.setType("java.lang.Integer");
//        return defaultConfig;
//    }
//
//    private IntersectionConfig<Integer> getIntersectionConfig() {
//        var intersectionConfig = new IntersectionConfig<Integer>();
//        intersectionConfig.setKey(key);
//        intersectionConfig.setValue(intersectionValue);
//        intersectionConfig.setCategory(category);
//        intersectionConfig.setUnits(units);
//        intersectionConfig.setDescription(description);
//        intersectionConfig.setIntersectionID(intersectionId);
//        intersectionConfig.setRoadRegulatorID(regionId);
//        intersectionConfig.setType("java.lang.Integer");
//        return intersectionConfig;}
}
