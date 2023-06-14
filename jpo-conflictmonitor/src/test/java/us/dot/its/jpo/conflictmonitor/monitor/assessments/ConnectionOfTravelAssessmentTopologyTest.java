package us.dot.its.jpo.conflictmonitor.monitor.assessments;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.ConnectionOfTravelNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.ConnectionOfTravelAssessmentTopology;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class ConnectionOfTravelAssessmentTopologyTest {
    String kafkaTopicConnectionOfTravelEvent = "topic.CmConnectionOfTravelEvent";
    String kafkaTopicConnectionOfTravelAssessment = "topic.CmConnectionOfTravelAssessment";
    String kafkaTopicConnectionOfTravelAssessmentNotification = "topic.CmConnectionOfTravelNotification";
    String ConnectionOfTravelEventKey = "12109";
    String ConnectionOfTravelEvent = "{\"eventGeneratedAt\":1673914223688,\"eventType\":\"ConnectionOfTravel\",\"timestamp\":0,\"roadRegulatorID\":-1,\"intersectionID\":12109,\"ingressLaneID\":12,\"egressLaneID\":5,\"connectionID\":1}";
    String ConnectionOfTravelMissingEvent = "{\"eventGeneratedAt\":1673914223688,\"eventType\":\"ConnectionOfTravel\",\"timestamp\":0,\"roadRegulatorID\":-1,\"intersectionID\":12109,\"ingressLaneID\":12,\"egressLaneID\":5,\"connectionID\":-1}";
    
    @Test
    public void testConnectionOfTravelAssessment() {
        ConnectionOfTravelAssessmentTopology assessment = new ConnectionOfTravelAssessmentTopology();
        ConnectionOfTravelAssessmentParameters parameters = new ConnectionOfTravelAssessmentParameters();
        parameters.setDebug(false);
        parameters.setConnectionOfTravelEventTopicName(kafkaTopicConnectionOfTravelEvent);
        parameters.setConnectionOfTravelAssessmentOutputTopicName(kafkaTopicConnectionOfTravelAssessment);
        parameters.setLookBackPeriodDays(1);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
        parameters.setMinimumNumberOfEvents(10);
        parameters.setConnectionOfTravelNotificationTopicName(kafkaTopicConnectionOfTravelAssessmentNotification);
        assessment.setParameters(parameters);


        Topology topology = assessment.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            TestInputTopic<String, String> inputTopic = driver.createInputTopic(
                kafkaTopicConnectionOfTravelEvent, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, ConnectionOfTravelAssessment> outputTopic = driver.createOutputTopic(
                kafkaTopicConnectionOfTravelAssessment, 
                Serdes.String().deserializer(), 
                JsonSerdes.ConnectionOfTravelAssessment().deserializer());
            
            inputTopic.pipeInput(ConnectionOfTravelEventKey, ConnectionOfTravelEvent);

            List<KeyValue<String, ConnectionOfTravelAssessment>> assessmentResults = outputTopic.readKeyValuesToList();
            
            assertEquals(assessmentResults.size(),1);

            ConnectionOfTravelAssessment output = assessmentResults.get(0).value;;
            List<ConnectionOfTravelAssessmentGroup> groups = output.getConnectionOfTravelAssessment();
            
            assertEquals(groups.size(), 1);

            ConnectionOfTravelAssessmentGroup group = groups.get(0);
            assertEquals(1, group.getConnectionID());
            assertEquals(5, group.getEgressLaneID());
            assertEquals(12, group.getIngressLaneID());
            assertEquals(1, group.getEventCount());
        }
    }

    @Test
    public void testConnectionOfTravelNotification() {
        ConnectionOfTravelAssessmentTopology assessment = new ConnectionOfTravelAssessmentTopology();
        ConnectionOfTravelAssessmentParameters parameters = new ConnectionOfTravelAssessmentParameters();
        parameters.setDebug(false);
        parameters.setConnectionOfTravelEventTopicName(kafkaTopicConnectionOfTravelEvent);
        parameters.setConnectionOfTravelAssessmentOutputTopicName(kafkaTopicConnectionOfTravelAssessment);
        parameters.setLookBackPeriodDays(1);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
        parameters.setMinimumNumberOfEvents(1);
        parameters.setConnectionOfTravelNotificationTopicName(kafkaTopicConnectionOfTravelAssessmentNotification);
        assessment.setParameters(parameters);


        Topology topology = assessment.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            TestInputTopic<String, String> inputTopic = driver.createInputTopic(
                kafkaTopicConnectionOfTravelEvent, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, ConnectionOfTravelNotification> outputTopic = driver.createOutputTopic(
                kafkaTopicConnectionOfTravelAssessmentNotification, 
                Serdes.String().deserializer(), 
                JsonSerdes.ConnectionOfTravelNotification().deserializer());
            
            inputTopic.pipeInput(ConnectionOfTravelEventKey, ConnectionOfTravelMissingEvent);

            List<KeyValue<String, ConnectionOfTravelNotification>> assessmentResults = outputTopic.readKeyValuesToList();
            
            assertEquals(assessmentResults.size(),1);

            ConnectionOfTravelNotification notification = assessmentResults.get(0).value;
            assertEquals(notification.getNotificationHeading(), "Connection of Travel Notification");
            assertEquals(notification.getNotificationType(), "ConnectionOfTravelNotification");
            assertEquals(notification.getNotificationText(), "Connection of Travel Notification, Unknown Lane connection between ingress lane: 12 and egress lane: 5.");

            ConnectionOfTravelAssessment notificationAssessment = notification.getAssessment();
            
            List<ConnectionOfTravelAssessmentGroup> groups = notificationAssessment.getConnectionOfTravelAssessment();
            
            assertEquals(groups.size(), 1);

            ConnectionOfTravelAssessmentGroup group = groups.get(0);
            assertEquals(-1,group.getConnectionID());
            assertEquals(5, group.getEgressLaneID());
            assertEquals(12, group.getIngressLaneID());
            assertEquals(1,group.getEventCount());
        }
    }
}