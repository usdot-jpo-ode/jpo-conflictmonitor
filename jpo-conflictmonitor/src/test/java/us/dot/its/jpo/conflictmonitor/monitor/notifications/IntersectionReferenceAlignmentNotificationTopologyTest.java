package us.dot.its.jpo.conflictmonitor.monitor.notifications;

import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.IntersectionReferenceAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.SignalStateEventAssessmentTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.notifications.IntersectionReferenceAlignmentNotificationTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_reference_alignment_notification.IntersectionReferenceAlignmentNotificationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IntersectionReferenceAlignmentNotificationTopologyTest {
    String kafkaTopicIntersectionReferenceAlignmentEvents = "topic.CmIntersectionReferenceAlignmentEvents";
    String kafkaTopicIntersectionReferenceAlignmentNotifications = "topic.CmIntersectionReferenceAlignmentNotifications";
    @Test
    public void testTopology() {
        IntersectionReferenceAlignmentNotificationTopology notification = new IntersectionReferenceAlignmentNotificationTopology();
        IntersectionReferenceAlignmentNotificationParameters parameters = new IntersectionReferenceAlignmentNotificationParameters();
        
        parameters.setDebug(false);
        parameters.setIntersectionReferenceAlignmentNotificationTopicName(kafkaTopicIntersectionReferenceAlignmentNotifications);
        parameters.setIntersectionReferenceAlignmentEventTopicName(kafkaTopicIntersectionReferenceAlignmentEvents);
        notification.setParameters(parameters);

        // SignalStateEventAssessmentParameters parameters = new SignalStateEventAssessmentParameters();
        // parameters.setDebug(false);
        // parameters.setSignalStateEventAssessmentOutputTopicName(kafkaTopicIntersectionReferenceAlignmentNotifications);
        // parameters.setSignalStateEventTopicName(kafkaTopicIntersectionReferenceAlignmentEvents);
        // parameters.setLookBackPeriodGraceTimeSeconds(30);
        // parameters.setLookBackPeriodDays(1);

        
        


        IntersectionReferenceAlignmentEvent event = new IntersectionReferenceAlignmentEvent();
        String eventKey = "";

        // props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        // SignalStateEventAssessmentTopology assessment = new SignalStateEventAssessmentTopology();
        // assessment.setParameters(parameters);
        Topology topology = notification.buildTopology();
        // Topology topology = notification.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            // TestInputTopic<String, IntersectionReferenceAlignmentEvent> inputTopic = driver.createInputTopic(
            //     kafkaTopicIntersectionReferenceAlignmentEvents, 
            //     Serdes.String().serializer(), 
            //     JsonSerdes.IntersectionReferenceAlignmentEvent().serializer());


            // TestOutputTopic<String, Notification> outputTopic = driver.createOutputTopic(
            //     kafkaTopicIntersectionReferenceAlignmentNotifications, 
            //     Serdes.String().deserializer(), 
            //     JsonSerdes.Notification().deserializer());
            
            // inputTopic.pipeInput(eventKey, event);

            // List<KeyValue<String, Notification>> notificationResults = outputTopic.readKeyValuesToList();
            
            // assertEquals(assessmentResults.size(),1);

            // ConnectionOfTravelAssessment output = assessmentResults.get(0).value;
            
            // List<ConnectionOfTravelAssessmentGroup> groups = output.getConnectionOfTravelAssessment();
            
            // assertEquals(groups.size(), 1);

            // ConnectionOfTravelAssessmentGroup group = groups.get(0);
            // assertEquals(1,group.getConnectionID());
            // assertEquals(5, group.getEgressLaneID());
            // assertEquals(12, group.getIngressLaneID());
            
        }
        assertEquals(0,0);
    }
}