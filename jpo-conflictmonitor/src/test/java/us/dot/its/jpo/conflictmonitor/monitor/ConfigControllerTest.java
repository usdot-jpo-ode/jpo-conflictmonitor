package us.dot.its.jpo.conflictmonitor.monitor;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.config.ConfigUpdateResult;
import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanes;
import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPair;
import us.dot.its.jpo.conflictmonitor.monitor.models.concurrent_permissive.ConnectedLanesPairList;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.Config;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfig;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.IntersectionConfigKey;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.UnitsEnum;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;
import us.dot.its.jpo.conflictmonitor.testutils.ConfigTestUtils;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Test using Spring Mock MVC, see https://spring.io/guides/gs/testing-web/
@WebMvcTest(ConfigController.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("testConfig")
public class ConfigControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ConfigControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigTopology configTopology;

    @MockBean(name = "createKafkaTopics")
    private KafkaAdmin.NewTopics createKafkaTopics;

    @MockBean
    private ConflictMonitorProperties conflictMonitorProperties;

    @Test
    public void testListDefaultConfigs() throws Exception {

        when(configTopology.mapDefaultConfigs()).thenReturn(ConfigTestUtils.getDefaultConfigMap());

        mockMvc.perform(get("/config/defaults"))
                .andDo(mvcResult -> logger.info("GET /config/defaults: {}", mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(ConfigTestUtils.key)));

    }

    @Test
    public void testListIntersectionConfigs() throws Exception {

        when(configTopology.mapIntersectionConfigs()).thenReturn(ConfigTestUtils.getIntersectionConfigMap());

        mockMvc.perform(get("/config/intersections"))
                .andDo(mvcResult -> logger.info("GET /config/intersections: {}", mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(ConfigTestUtils.key)))
                .andExpect(content().string(containsString(String.format("\"%s\"", ConfigTestUtils.intersectionId))));

    }

    @Test
    public void testGetDefaultConfig() throws Exception {

        final String key = ConfigTestUtils.key;

        when(configTopology.getDefaultConfig(key)).thenReturn(ConfigTestUtils.getDefaultConfig(ConfigTestUtils.defaultValue, Integer.class.getName()));

        mockMvc.perform(get("/config/default/{key}", key))
                .andDo(mvcResult -> logger.info("GET /config/default/{}: {}", key, mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(ConfigTestUtils.key)));

    }

    @Test
    public void testGetIntersectionConfig() throws Exception {

        final String key = ConfigTestUtils.key;
        final int region = ConfigTestUtils.regionId;
        final int intersectionId = ConfigTestUtils.intersectionId;
        final int value = 10;
        final var intersectionKey = new IntersectionConfigKey(region, intersectionId, key);

        final var config = Optional.of(ConfigTestUtils.getIntersectionConfig(value, Integer.class.getName()));
        when(configTopology.getIntersectionConfig(intersectionKey)).thenReturn(Optional.of(ConfigTestUtils.getIntersectionConfig(value, Integer.class.getName())));

        mockMvc.perform(get("/config/intersection/{region}/{intersectionId}/{key}", region, intersectionId, key))
                .andDo(mvcResult -> logger.info("GET /config/intersection/{}/{}/{}: {}", region, intersectionId, key, mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(config.get().getIntersectionKey())));

    }

    @Test
    public void testGetIntersectionConfig_NoRegion() throws Exception {

        final String key = ConfigTestUtils.key;
        final int intersectionId = ConfigTestUtils.intersectionId;
        final var intersectionKey = new IntersectionConfigKey(-1, intersectionId, key);
        final int value = 10;

        final var config = Optional.of(ConfigTestUtils.getIntersectionConfig_NoRegion());
        when(configTopology.getIntersectionConfig(intersectionKey)).thenReturn(Optional.of(ConfigTestUtils.getIntersectionConfig_NoRegion(value, Integer.class.getName())));

        mockMvc.perform(get("/config/intersection/{intersectionId}/{key}", intersectionId, key))
                .andDo(mvcResult -> logger.info("GET /config/intersection/{}/{}: {}", intersectionId, key, mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(config.get().getIntersectionKey())));

    }

    @Test
    public void testSaveDefaultConfig() throws Exception {

        final var config = ConfigTestUtils.getCustomConfig();
        when(configTopology.updateCustomConfig(config)).thenReturn(ConfigTestUtils.getUpdateResult(config));

        mockMvc.perform(
                    post("/config/default/{key}", config.getKey())
                        .content(config.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(mvcResult -> logger.info("POST /config/default/{}: {}", config.getKey(), mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(config.getKey())))
                .andExpect(content().string(containsString(ConfigUpdateResult.Result.UPDATED.toString())));

    }

    @Test
    public void testSaveIntersectionConfig() throws Exception {

        final String key = ConfigTestUtils.key;
        final int region = ConfigTestUtils.regionId;
        final int intersectionId = ConfigTestUtils.intersectionId;
        final var config = ConfigTestUtils.getIntersectionConfig();
        when(configTopology.updateIntersectionConfig(config)).thenReturn(ConfigTestUtils.getUpdateResult(config));

        mockMvc.perform(
                    post("/config/intersection/{region}/{intersectionId}/{key}", region, intersectionId, key)
                        .content(config.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(mvcResult -> logger.info("POST /config/intersection/{}/{}/{}: {}", region, intersectionId, key, mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(config.getIntersectionKey())))
                .andExpect(content().string(containsString(ConfigUpdateResult.Result.UPDATED.toString())));

    }

    @Test
    public void testSaveIntersectionConfig_NoRegion() throws Exception {

        final String key = ConfigTestUtils.key;
        final int intersectionId = ConfigTestUtils.intersectionId;
        final var config = ConfigTestUtils.getIntersectionConfig_NoRegion();
        when(configTopology.updateIntersectionConfig(config)).thenReturn(ConfigTestUtils.getUpdateResult(config));

        mockMvc.perform(
                        post("/config/intersection/{intersectionId}/{key}", intersectionId, key)
                                .content(config.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(mvcResult -> logger.info("POST /config/intersection/{}/{}: {}", intersectionId, key, mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(config.getIntersectionKey())))
                .andExpect(content().string(containsString(ConfigUpdateResult.Result.UPDATED.toString())));

    }

    @Test
    public void testSaveIntersectionConfig_ConcurrentPermissive() throws Exception {
        final String key = "map.spat.message.assessment.concurrentPermissiveList";
        final int intersectionId = ConfigTestUtils.intersectionId;
        final int region = ConfigTestUtils.regionId;
        final String description = "Concurrent permissive lanes";
        final ConnectedLanesPairList pairList = new ConnectedLanesPairList();
        ConnectedLanesPair pair1 = new ConnectedLanesPair();
        pair1.setFirst(new ConnectedLanes(1, 2));
        pair1.setSecond(new ConnectedLanes(3, 4));
        ConnectedLanesPair pair2 = new ConnectedLanesPair();
        pair2.setFirst(new ConnectedLanes(5, 6));
        pair2.setSecond(new ConnectedLanes(7, 8));
        pairList.add(pair1);
        pairList.add(pair2);

        final var config = new IntersectionConfig<ConnectedLanesPairList>();
        config.setKey(key);
        config.setValue(pairList);
        config.setCategory("test");
        config.setUnits(UnitsEnum.NONE);
        config.setDescription(description);
        config.setIntersectionID(intersectionId);
        config.setRoadRegulatorID(region);
        config.setType(ConnectedLanesPairList.class.getName());

        final ConfigUpdateResult<ConnectedLanesPairList> expectUpdateResult = ConfigTestUtils.getUpdateResult(config);
        logger.info("Expect update result: {}", expectUpdateResult);

        when(configTopology.updateIntersectionConfig(any())).thenReturn((ConfigUpdateResult)expectUpdateResult);

        mockMvc.perform(
                        post("/config/intersection/{region}/{intersectionId}/{key}", region, intersectionId, key)
                                .content(config.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.ALL)
                ).andDo(mvcResult -> logger.info("POST /config/intersection/{}/{}/{}: {}", region, intersectionId, key, mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(config.getIntersectionKey())))
                .andExpect(content().string(containsString(ConfigUpdateResult.Result.UPDATED.toString())));

    }
}
