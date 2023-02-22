package us.dot.its.jpo.conflictmonitor.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.*;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.ValidationConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;

import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
public class ConflictMonitorPropertiesTest {

    private final static Logger logger = LoggerFactory.getLogger(ConflictMonitorPropertiesTest.class);
    
    @Autowired
    private ConflictMonitorProperties properties;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Test
    public void testPropertiesInjected() {
        assertThat(properties, notNullValue());
    }

    @Test
    public void testMapBroadcastRateAlgorithm() {
        assertThat(properties.getMapValidationAlgorithm(), anyOf(equalTo(DEFAULT_MAP_VALIDATION_ALGORITHM), equalTo(ALTERNATE_MAP_VALIDATION_ALGORITHM)));
    }

    @Test
    public void testMapBroadcastRateParameters() {
        var props = properties.getMapValidationParameters();
        assertThat(props, notNullValue());
        assertThat(props.getInputTopicName(), equalTo("topic.ProcessedMap"));
        assertThat(props.getBroadcastRateTopicName(), equalTo("topic.CmMapBroadcastRateEvents"));
        assertThat(props.getMinimumDataTopicName(), equalTo("topic.CmMapMinimumDataEvents"));
    }

    @Test
    public void testSpatBroadcastRateAlgorithm() {
        assertThat(properties.getSpatValidationAlgorithm(), anyOf(equalTo(DEFAULT_SPAT_VALIDATION_ALGORITHM), equalTo(ALTERNATE_SPAT_VALIDATION_ALGORITHM)));
    }

    @Test
    public void testSpatBroadcastRateParameters() {
        var props = properties.getSpatValidationParameters();
        assertThat(props, notNullValue());
        assertThat(props.getInputTopicName(), equalTo("topic.ProcessedSpat"));
        assertThat(props.getBroadcastRateTopicName(), equalTo("topic.CmSpatBroadcastRateEvents"));
        assertThat(props.getMinimumDataTopicName(), equalTo("topic.CmSpatMinimumDataEvents"));
    }

    @Test
    public void testKafkaAdminInjected() {
        assertThat(kafkaAdmin, notNullValue()); 
    }

    @Test
    public void testKafkaAdminHasBootstrapServerProperty() {
        var kProps = kafkaAdmin.getConfigurationProperties();
        assertThat(kProps, notNullValue());
        logger.info("KafkaAdmin exists: Props: {}", kProps);
        assertThat(kProps, hasKey("bootstrap.servers"));
        var servers = kProps.get("bootstrap.servers");
        assertThat(servers, instanceOf(List.class));
        assertThat((List<String>)servers, hasItem("localhost:9092"));
    }
    
}
