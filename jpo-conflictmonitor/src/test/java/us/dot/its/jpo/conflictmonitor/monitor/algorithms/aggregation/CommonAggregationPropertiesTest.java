package us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
@Slf4j
public class CommonAggregationPropertiesTest {

    @Autowired
    private CommonAggregationParameters params;

    @Test
    public void testPropertiesLoaded() {
        log.info("CommonAggregationProperties: {}", params);
        assertThat(params, notNullValue());
        assertThat(params, hasProperty("interval", greaterThan(0)));
        assertThat(params, hasProperty("intervalUnits", instanceOf(CommonAggregationParameters.IntervalUnits.class)));
    }
}
