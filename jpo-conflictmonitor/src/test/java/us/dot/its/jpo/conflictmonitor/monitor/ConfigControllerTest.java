package us.dot.its.jpo.conflictmonitor.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import us.dot.its.jpo.conflictmonitor.monitor.models.config.DefaultConfigMap;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ActiveProfiles({"testConfig"})
@DirtiesContext
public class ConfigControllerTest {

    @Autowired
    private ConfigController configController;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        assertThat(configController, notNullValue());
    }
    @Test
    public void testListDefaultConfigs() {
        final String url = String.format("http://localhost:%d/config/defaults", port);
        var response = restTemplate.getForEntity(
                url, String.class);
        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

}
