package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static us.dot.its.jpo.conflictmonitor.testutils.ConfigTestUtils.*;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUpdateResult;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.*;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;
import us.dot.its.jpo.conflictmonitor.testutils.ConfigTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class ConfigTopologyTest {



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

    @Mock
    ReadOnlyKeyValueStore<String, DefaultConfig<Integer>> defaultStore;



    @Mock
    KafkaTemplate<String, String> mockKafkaTemplate;

    @Mock
    ReadOnlyKeyValueStore<IntersectionConfigKey, IntersectionConfig<Integer>> intersectionStore;



    @Mock
    KeyValueIterator<IntersectionConfigKey, IntersectionConfig<Integer>> intersectionIterator;

    @Mock
    KeyValueIterator<String, DefaultConfig<Integer>> defaultIterator;



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




    @Test
    public void testUpdateDefaultConfig() throws Exception {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        final DefaultConfig defaultConfig = ConfigTestUtils.getDefaultConfig();
        configTopology.setStreams(streams);
        configTopology.setKafkaTemplate(mockKafkaTemplate);
        configTopology.updateDefaultConfig(defaultConfig);
        verify(mockKafkaTemplate).send(anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdateCustomConfig() throws Exception {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        when(streams.store(any())).thenReturn(defaultStore);
        final DefaultConfig defaultConfig = ConfigTestUtils.getDefaultConfig();
        when(defaultStore.get(anyString())).thenReturn(defaultConfig);
        configTopology.setStreams(streams);
        configTopology.setKafkaTemplate(mockKafkaTemplate);
        final DefaultConfig customConfig = ConfigTestUtils.getCustomConfig();
        var result = configTopology.updateCustomConfig(customConfig);
        assertThat(result.getResult(), equalTo(ConfigUpdateResult.Result.UPDATED));
    }

    @Test
    public void testGetIntersectionConfig() throws Exception {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        configTopology.setStreams(streams);
        when(streams.store(any())).thenReturn(intersectionStore);
        final IntersectionConfig<Integer> intersectionConfig = ConfigTestUtils.getIntersectionConfig();
        when(intersectionStore.all()).thenReturn(intersectionIterator);
        when(intersectionIterator.hasNext()).thenReturn(true, false);
        when(intersectionIterator.next()).thenReturn(new KeyValue<>(intersectionConfig.intersectionKey(), intersectionConfig));
        var result = configTopology.getIntersectionConfig(intersectionConfig.intersectionKey());
        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(intersectionConfig));
    }

    @Test
    public void testUpdateIntersectionConfig() throws Exception {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        configTopology.setStreams(streams);
        configTopology.setKafkaTemplate(mockKafkaTemplate);

        when(streams.store(any())).thenReturn(defaultStore, intersectionStore);
        final DefaultConfig defaultConfig = ConfigTestUtils.getDefaultConfig();
        when(defaultStore.get(anyString())).thenReturn(defaultConfig);

        final IntersectionConfig<Integer> intersectionConfig = ConfigTestUtils.getIntersectionConfig();
        when(intersectionStore.all()).thenReturn(intersectionIterator);
        when(intersectionIterator.hasNext()).thenReturn(true, false);
        when(intersectionIterator.next()).thenReturn(new KeyValue<>(intersectionConfig.intersectionKey(), intersectionConfig));

        var result = configTopology.updateIntersectionConfig(intersectionConfig);
        assertThat(result.getResult(), equalTo(ConfigUpdateResult.Result.UPDATED));
        verify(mockKafkaTemplate).send(anyString(), anyString(), anyString());
    }

    @Test
    public void testUpdateIntersectionConfig_NoRegion() throws Exception {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        configTopology.setStreams(streams);
        configTopology.setKafkaTemplate(mockKafkaTemplate);

        when(streams.store(any())).thenReturn(defaultStore, intersectionStore);
        final DefaultConfig defaultConfig = ConfigTestUtils.getDefaultConfig();
        when(defaultStore.get(anyString())).thenReturn(defaultConfig);

        final IntersectionConfig<Integer> intersectionConfig = ConfigTestUtils.getIntersectionConfig_NoRegion();
        when(intersectionStore.all()).thenReturn(intersectionIterator);
        when(intersectionIterator.hasNext()).thenReturn(true, false);
        when(intersectionIterator.next()).thenReturn(new KeyValue<>(intersectionConfig.intersectionKey(), intersectionConfig));

        var result = configTopology.updateIntersectionConfig(intersectionConfig);
        assertThat(result.getResult(), equalTo(ConfigUpdateResult.Result.UPDATED));
        verify(mockKafkaTemplate).send(anyString(), anyString(), anyString());
    }

    @Test
    public void testListIntersectionConfigs() throws Exception {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        configTopology.setStreams(streams);
        configTopology.setKafkaTemplate(mockKafkaTemplate);

        when(streams.store(any())).thenReturn(intersectionStore);
        final IntersectionConfig<Integer> intersectionConfig = ConfigTestUtils.getIntersectionConfig();
        when(intersectionStore.all()).thenReturn(intersectionIterator);
        when(intersectionIterator.hasNext()).thenReturn(true, false);
        when(intersectionIterator.next()).thenReturn(new KeyValue<>(intersectionConfig.intersectionKey(), intersectionConfig));
        Collection<IntersectionConfig<?>> configList = configTopology.listIntersectionConfigs(intersectionConfig.getKey());

        assertThat(configList, hasSize(1));
    }

    @Test
    public void testMapIntersectionConfigs() throws Exception {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        configTopology.setStreams(streams);
        configTopology.setKafkaTemplate(mockKafkaTemplate);

        when(streams.store(any())).thenReturn(intersectionStore);
        final IntersectionConfig<Integer> intersectionConfig = ConfigTestUtils.getIntersectionConfig();
        when(intersectionStore.all()).thenReturn(intersectionIterator);
        when(intersectionIterator.hasNext()).thenReturn(true, false);
        when(intersectionIterator.next()).thenReturn(new KeyValue<>(intersectionConfig.intersectionKey(), intersectionConfig));
        var configMap = configTopology.mapIntersectionConfigs();

        assertThat(configMap.listConfigs(), hasSize(1));
    }

    @Test
    public void testMapDefaultConfigs() {
        var configTopology = new ConfigTopology();
        var parameters = ConfigTestUtils.getParameters();
        configTopology.setParameters(parameters);
        var streams = mock(KafkaStreams.class);
        when(streams.store(any())).thenReturn(defaultStore);
        final DefaultConfig<Integer> defaultConfig = new DefaultConfig<>();
        when(defaultStore.all()).thenReturn(defaultIterator);
        when(defaultIterator.hasNext()).thenReturn(true, false);
        when(defaultIterator.next()).thenReturn(new KeyValue<>(key, defaultConfig));
        configTopology.setStreams(streams);
        var result = configTopology.mapDefaultConfigs();
        assertThat(result.keySet(), hasSize(1));
    }
}
