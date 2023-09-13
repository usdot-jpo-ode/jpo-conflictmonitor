package us.dot.its.jpo.conflictmonitor.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.config.ConfigTopology;
import us.dot.its.jpo.conflictmonitor.testutils.ConfigTestUtils;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andDo(mvcResult -> {
                    logger.info("Response: {}", mvcResult.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(ConfigTestUtils.key)));

    }

//    @Test
//    public void testListIntersectionConfigs() {
//        final String url = String.format("http://localhost:%d/config/intersections", port);
//        var response = restTemplate.getForEntity(
//                url, String.class);
//        assertThat(response, notNullValue());
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//    }
//
//    @Test
//    public void testGetDefaultConfig() {
//        final String url = String.format("http://localhost:%d/config/default/spat.validation.lowerBound", port);
//        var response = restTemplate.getForEntity(
//                url, String.class);
//        logger.info("Response: {}", response.getBody());
//        assertThat(response, notNullValue());
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//    }
//
//    @Test
//    public void testGetIntersectionConfig() {
//        final String url = String.format("http://localhost:%d/config/intersection/1/111111/spat.validation.lowerBound", port);
//        var response = restTemplate.getForEntity(
//                url, String.class);
//        logger.info("Response: {}", response.getBody());
//        assertThat(response, notNullValue());
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//    }
//
//    @Test
//    public void testGetIntersectionConfig_NoRegion() {
//        final String url = String.format("http://localhost:%d/config/intersection/111111/spat.validation.lowerBound", port);
//        var response = restTemplate.getForEntity(
//                url, String.class);
//        logger.info("Response: {}", response.getBody());
//        assertThat(response, notNullValue());
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//    }
//
//    @Test
//    public void testSaveDefaultConfig() {
//        final String url = String.format("http://localhost:%d/config/default/spat.validation.lowerBound", port);
//        var response = restTemplate.postForEntity(
//                url, ConfigTestUtils.getCustomConfig(), String.class);
//        logger.info("Response: {}", response.getBody());
//        assertThat(response, notNullValue());
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//    }
//
//    @Test
//    public void testSaveIntersectionConfig() {
//        final String url = String.format("http://localhost:%d/config/intersection/1/111111/spat.validation.lowerBound", port);
//        var response = restTemplate.postForEntity(
//                url, ConfigTestUtils.getIntersectionConfig(), String.class);
//        logger.info("Response: {}", response.getBody());
//        assertThat(response, notNullValue());
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//    }
//
//    @Test
//    public void testSaveIntersectionConfig_NoRegion() {
//        final String url = String.format("http://localhost:%d/config/intersection/111111/spat.validation.lowerBound", port);
//        var response = restTemplate.postForEntity(
//                url, ConfigTestUtils.getIntersectionConfig_NoRegion(), String.class);
//        logger.info("Response: {}", response.getBody());
//        assertThat(response, notNullValue());
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//    }
}
