package us.dot.its.jpo.conflictmonitor;

import org.apache.kafka.streams.StreamsConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.ValidationConstants.*;

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
        assertThat((List<String>)servers, anyOf(hasItem("localhost:9092"), hasItem("127.0.0.1:9092")));
    }

    @Test
    public void testCreateStreamProperties() {
        final String streamName = "testStream";
        Properties streamProps = properties.createStreamProperties(streamName);
        assertThat(streamProps, notNullValue());
        assertThat(streamProps.getProperty(StreamsConfig.APPLICATION_ID_CONFIG), equalTo(streamName));
        assertThat(streamProps.getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG), notNullValue());
    }

    @Test
    public void testGetProperties() {
        assertThat(properties.getMapValidationAlgorithmFactory(), notNullValue());
        assertThat(properties.getSpatValidationAlgorithmFactory(), notNullValue());
        assertThat(properties.getLaneDirectionOfTravelAlgorithmFactory(), notNullValue());
        assertThat(properties.getLaneDirectionOfTravelParameters(), notNullValue());
        assertThat(properties.getLaneDirectionOfTravelAlgorithm(), notNullValue());
        assertThat(properties.getConnectionOfTravelAlgorithmFactory(), notNullValue());
        assertThat(properties.getConnectionOfTravelAlgorithm(), notNullValue());
        assertThat(properties.getConnectionOfTravelParameters(), notNullValue());
        assertThat(properties.getSignalStateVehicleCrossesAlgorithmFactory(), notNullValue());
        assertThat(properties.getSignalStateVehicleCrossesAlgorithm(), notNullValue());
        assertThat(properties.getSignalStateVehicleCrossesParameters(), notNullValue());
        assertThat(properties.getSignalStateVehicleStopsAlgorithmFactory(), notNullValue());
        assertThat(properties.getSignalStateVehicleStopsAlgorithm(), notNullValue());
        assertThat(properties.getSignalStateVehicleStopsParameters(), notNullValue());
        assertThat(properties.getMapSpatMessageAssessmentAlgorithmFactory(), notNullValue());
        assertThat(properties.getMapSpatMessageAssessmentParameters(), notNullValue());
        assertThat(properties.getMapSpatMessageAssessmentAlgorithm(), notNullValue());
        assertThat(properties.getSpatTimeChangeDetailsAlgorithmFactory(), notNullValue());
        assertThat(properties.getSpatTimeChangeDetailsAlgorithm(), notNullValue());
        assertThat(properties.getSpatTimeChangeDetailsNotificationAlgorithm(), notNullValue());
        assertThat(properties.getSpatTimeChangeDetailsParameters(), notNullValue());
        // assertThat(properties.getMapTimeChangeDetailsAlgorithmFactory(), notNullValue());
        // assertThat(properties.getMapTimeChangeDetailsAlgorithm(), notNullValue());
        // assertThat(properties.getMapTimeChangeDetailsParameters(), notNullValue());
        assertThat(properties.getSignalStateEventAssessmentAlgorithmFactory(), notNullValue());
        assertThat(properties.getSignalStateEventAssessmentAlgorithm(), notNullValue());
        assertThat(properties.getSignalStateEventAssessmentAlgorithmParameters(), notNullValue());
        assertThat(properties.getLaneDirectionOfTravelAssessmentAlgorithmFactory(), notNullValue());
        assertThat(properties.getLaneDirectionOfTravelAssessmentAlgorithm(), notNullValue());
        assertThat(properties.getLaneDirectionOfTravelAssessmentAlgorithmParameters(), notNullValue());
        assertThat(properties.getConnectionOfTravelAssessmentAlgorithmFactory(), notNullValue());
        assertThat(properties.getConnectionOfTravelAssessmentAlgorithm(), notNullValue());
        assertThat(properties.getConnectionOfTravelAssessmentAlgorithmParameters(), notNullValue());
        assertThat(properties.getRepartitionAlgorithmFactory(), notNullValue());
        assertThat(properties.getRepartitionAlgorithm(), notNullValue());
        assertThat(properties.getRepartitionAlgorithmParameters(), notNullValue());
        assertThat(properties.getIntersectionEventAlgorithmFactory(), notNullValue());
        assertThat(properties.getIntersectionEventAlgorithm(), notNullValue());
        assertThat(properties.getKafkaStateChangeEventTopic(), notNullValue());
        assertThat(properties.getAppHealthNotificationTopic(), notNullValue());
        assertThat(properties.getVersion(), notNullValue());
        assertThat(properties.getKafkaBrokers(), notNullValue());
        assertThat(properties.getHostId(), notNullValue());
        assertThat(properties.getConnectURL(), notNullValue());
        // assertThat(properties.getDbHostIP(), notNullValue());
        assertThat(properties.getKafkaBrokerIP(), notNullValue());
        assertThat(properties.getKafkaTopicOdeBsmJson(), notNullValue());
        assertThat(properties.getKafkaTopicOdeMapJson(), notNullValue());
        assertThat(properties.getKafkaTopicCmBsmEvent(), notNullValue());
        assertThat(properties.getKafkaTopicCmConnectionOfTravelEvent(), notNullValue());
        assertThat(properties.getKafkaTopicCmLaneDirectionOfTravelEvent(), notNullValue());
        assertThat(properties.getKafkaTopicCmSignalStateEvent(), notNullValue());
        assertThat(properties.getKafkaTopicMapGeoJson(), notNullValue());
        assertThat(properties.getKafkaTopicProcessedMap(), notNullValue());
        assertThat(properties.getKafkaTopicProcessedSpat(), notNullValue());
        assertThat(properties.getKafakTopicCmVehicleStopEvent(), notNullValue());
        assertThat(properties.getKafkaTopicBsmRepartition(), notNullValue());
        assertThat(properties.getKafkaTopicSpatGeoJson(), notNullValue());
        assertThat(properties.getBuildProperties(), notNullValue());
        assertThat(properties.getEnv(), notNullValue());

        
    }

    @Test
    public void testGetProperty() {
        assertThat(properties.getProperty("version"), notNullValue());   
    }

    @Test
    public void testGetProperty_DefaultInt() {
        assertThat(properties.getProperty("server.port", 0), allOf(notNullValue(), not(equalTo(0))));
    }

    @Test
    public void testGetProperty_DefaultString() {
        assertThat(properties.getProperty("artifactId", "default"), allOf(notNullValue(), not(equalTo("default"))));
    }


    
}
