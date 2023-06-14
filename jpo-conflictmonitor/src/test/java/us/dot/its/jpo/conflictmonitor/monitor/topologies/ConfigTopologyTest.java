package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import lombok.Getter;
import lombok.Setter;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.RsuConfigKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

public class ConfigTopologyTest {

    final String defaultTopicName = "topic.CmDefaultConfig";
    final String intersectionTopicName = "topic.CmIntersectionConfig";
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
            
            var defaultTopic = driver.createInputTopic(defaultTopicName,
                    Serdes.String().serializer(),
                    JsonSerdes.DefaultConfig().serializer()
            );

            var intersectionTopic = driver.createInputTopic(intersectionTopicName,
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

    private ConfigParameters getParameters() {
        var parameters = new ConfigParameters();
        parameters.setDefaultTopicName(defaultTopicName);
        parameters.setIntersectionTopicName(intersectionTopicName);
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
