package us.dot.its.jpo.conflictmonitor.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.KafkaConfiguration;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.broadcast_rate.BroadcastRateConstants.*;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class KafkaConfigurationTest {

    private final static Logger logger = LoggerFactory.getLogger(ConflictMonitorPropertiesTest.class);

    @Autowired
    private KafkaConfiguration kafkaConfig;

    @Test
    public void testKafkaConfigurationInjected() {
        assertThat(kafkaConfig, notNullValue());
    }

    @Test
    public void testConfigurationsLoaded() {
        assertThat(kafkaConfig.getNumPartitions(), greaterThan(0));
        assertThat(kafkaConfig.getNumReplicas(), greaterThan(0));
        assertThat(kafkaConfig.getCreateTopics(), notNullValue());
    }
    
}
