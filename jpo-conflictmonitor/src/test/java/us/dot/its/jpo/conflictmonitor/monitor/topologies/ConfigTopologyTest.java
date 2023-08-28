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

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTopologyTest {

    final String defaultTableName = "topic.CmDefaultConfigTable";
    final String defaultStateStore = "default-config";
    final String intersectionStateStore = "intersection-config";
    final String intersectionTableName = "topic.CmIntersectionConfigTable";

    final String key = "key";
    final int defaultValue = 10;
    final String category = "category";
    final UnitsEnum units = UnitsEnum.SECONDS;
    final String description = "description";
    final String rsuId = "127.0.0.1";
    final int intersectionId = 11111;
    final int regionId = 1;
    final int intersectionValue = 12;


    @Test
    public void testConfigTopology() {

        
        var configTopology = new ConfigTopology();
        var configParams = getParameters();
        var streamsConfig = new Properties();
        configTopology.setParameters(configParams);


        var topology = configTopology.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {
            
            var defaultTopic = driver.createInputTopic(defaultTableName,
                    Serdes.String().serializer(),
                    JsonSerdes.DefaultConfig().serializer()
            );

            var intersectionTopic = driver.createInputTopic(intersectionTableName,
                    Serdes.String().serializer(),
                    JsonSerdes.IntersectionConfig().serializer()
            );

            var intersectionTable = driver.createOutputTopic(intersectionTableName,
                    JsonSerdes.RsuConfigKey().deserializer(),
                    JsonSerdes.IntersectionConfig().deserializer()
            );

            final var defaultConfig = getDefaultConfig();
            final var intersectionConfig = getIntersectionConfig();

            defaultTopic.pipeInput(key, defaultConfig);
            var defaultStore = driver.getKeyValueStore(defaultStateStore);
            // var iterator = defaultStore.all();
            // while (iterator.hasNext()) {
            //     var item = iterator.next();
            //     System.out.println(item.key + " " + item.value);
            // }
            var defaultResult = defaultStore.get(key);
            
            assertThat("default state store", defaultResult, equalTo(defaultConfig));            

            intersectionTopic.pipeInput(intersectionConfig);
            var intersectionStore = driver.<RsuConfigKey, IntersectionConfig<?>>getKeyValueStore(intersectionStateStore);
            var intersectionResult = intersectionStore.get(new RsuConfigKey(rsuId, key));
            assertThat("intersection state store", intersectionResult, equalTo(intersectionConfig));

            var intersectionTableList = intersectionTable.readKeyValuesToList();
            assertThat(intersectionTableList, hasSize(equalTo(1)));
            var tableItem = intersectionTableList.get(0);
            assertThat("intersection table key", tableItem.key, equalTo(new RsuConfigKey(rsuId, key)));
            assertThat("intersection table value", tableItem.value, equalTo(intersectionConfig));

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
        var parameters = getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        when(streams.store(any())).thenReturn(defaultStore);
        final DefaultConfig<Integer> defaultConfig = new DefaultConfig<>();
        when(defaultStore.get(anyString())).thenReturn(defaultConfig);
        configTopology.setStreams(streams);
        var result = configTopology.getDefaultConfig(key);
        assertThat(result, equalTo(defaultConfig));
    }



    private ConfigParameters getParameters() {
        var parameters = new ConfigParameters();
        parameters.setDefaultTopicName(defaultTableName);
        parameters.setDefaultStateStore(defaultStateStore);
        parameters.setIntersectionStateStore(intersectionStateStore);
        parameters.setIntersectionTableName(intersectionTableName);
        return parameters;
    }
    
    private DefaultConfig<Integer> getDefaultConfig() {
        var defaultConfig = new DefaultConfig<Integer>();
        defaultConfig.setKey(key);
        defaultConfig.setValue(defaultValue);
        defaultConfig.setCategory(category);
        defaultConfig.setUnits(units);
        defaultConfig.setDescription(description);
        defaultConfig.setType("java.lang.Integer");
        return defaultConfig;
    }

    private IntersectionConfig<Integer> getIntersectionConfig() {
        var intersectionConfig = new IntersectionConfig<Integer>();
        intersectionConfig.setKey(key);
        intersectionConfig.setValue(intersectionValue);
        intersectionConfig.setCategory(category);
        intersectionConfig.setUnits(units);
        intersectionConfig.setDescription(description);
        intersectionConfig.setRsuID(rsuId);
        intersectionConfig.setIntersectionID(intersectionId);
        intersectionConfig.setRoadRegulatorID(regionId);
        intersectionConfig.setType("java.lang.Integer");
        return intersectionConfig;}
}
