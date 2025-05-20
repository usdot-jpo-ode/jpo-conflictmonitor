package us.dot.its.jpo.conflictmonitor.monitor.assessments;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.StopLinePassageNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.StopLinePassageAssessmentTopology;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class StopLinePassageAssessmentTopologyTest {
    String kafkaTopicStopLinePassageEvent = "topic.CmStopLinePassageEvent";
    String kafkaTopicStopLinePassageAssessment = "topic.CmStopLinePassageAssessment";
    String kafkaTopicStopLinePassageNotification = "topic.CmStopLinePassageNotification";
    String stopLinePassageEventKey = "12109";
    String stopLinePassageEvent = "{\"eventGeneratedAt\":1673974273330,\"eventType\":\"StopLinePassage\",\"timestamp\":1655493260761,\"roadRegulatorID\":-1,\"ingressLane\":12,\"egressLane\":5,\"connectionID\":1,\"eventState\":\"STOP_AND_REMAIN\",\"vehicleID\":\"E6A99808\",\"latitude\":-105.091055,\"longitude\":-105.091055,\"heading\":169.4,\"speed\":22.64,\"signalGroup\":6}";
    String stopLinePassageEvent2 = "{\"eventGeneratedAt\":1673974273330,\"eventType\":\"StopLinePassage\",\"timestamp\":1655493260761,\"roadRegulatorID\":-1,\"ingressLane\":12,\"egressLane\":5,\"connectionID\":1,\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"vehicleID\":\"E6A99808\",\"latitude\":-105.091055,\"longitude\":-105.091055,\"heading\":169.4,\"speed\":22.64,\"signalGroup\":6}";
    @Test
    public void testTopology() {
        StopLinePassageAssessmentTopology assessment = new StopLinePassageAssessmentTopology();
        StopLinePassageAssessmentParameters parameters = new StopLinePassageAssessmentParameters();
        parameters.setDebug(false);
        parameters.setStopLinePassageAssessmentOutputTopicName(kafkaTopicStopLinePassageAssessment);
        parameters.setStopLinePassageEventTopicName(kafkaTopicStopLinePassageEvent);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
        parameters.setLookBackPeriodDays(1);
        parameters.setStopLinePassageNotificationOutputTopicName(kafkaTopicStopLinePassageNotification);
        parameters.setMinimumEventsToNotify(2);

        
        assessment.setParameters(parameters);


        Topology topology = assessment.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            TestInputTopic<String, String> inputTopic = driver.createInputTopic(
                kafkaTopicStopLinePassageEvent, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, StopLinePassageAssessment> outputTopic = driver.createOutputTopic(
                kafkaTopicStopLinePassageAssessment, 
                Serdes.String().deserializer(), 
                JsonSerdes.StopLinePassageAssessment().deserializer());
            
            inputTopic.pipeInput(stopLinePassageEventKey, stopLinePassageEvent);
            inputTopic.pipeInput(stopLinePassageEventKey, stopLinePassageEvent2);

            List<KeyValue<String, StopLinePassageAssessment>> assessmentResults = outputTopic.readKeyValuesToList();
            
            assertEquals(assessmentResults.size(),2);

            StopLinePassageAssessment output = assessmentResults.get(1).value;

            List<StopLinePassageAssessmentGroup> groups = output.getStopLinePassageAssessmentGroup();

            
            assertEquals(groups.size(), 1);
            
            StopLinePassageAssessmentGroup group = groups.get(0);
            assertEquals(group.getGreenEvents(), 1);
            assertEquals(group.getRedEvents(), 1);
            assertEquals(group.getYellowEvents(), 0);
            assertEquals(group.getDarkEvents(), 0);
            assertEquals(group.getSignalGroup(), 6);
        }
    }


    @Test
    public void testNotification() {
        StopLinePassageAssessmentTopology assessment = new StopLinePassageAssessmentTopology();
        StopLinePassageAssessmentParameters parameters = new StopLinePassageAssessmentParameters();
        parameters.setDebug(false);
        parameters.setStopLinePassageAssessmentOutputTopicName(kafkaTopicStopLinePassageAssessment);
        parameters.setStopLinePassageEventTopicName(kafkaTopicStopLinePassageEvent);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
        parameters.setLookBackPeriodDays(1);
        parameters.setStopLinePassageNotificationOutputTopicName(kafkaTopicStopLinePassageNotification);
        parameters.setMinimumEventsToNotify(2);
        parameters.setRedLightPercentToNotify(0);
        parameters.setYellowLightPercentToNotify(0);

        
        assessment.setParameters(parameters);


        Topology topology = assessment.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            TestInputTopic<String, String> inputTopic = driver.createInputTopic(
                kafkaTopicStopLinePassageEvent, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, StopLinePassageNotification> outputTopic = driver.createOutputTopic(
                kafkaTopicStopLinePassageNotification, 
                Serdes.String().deserializer(),
                JsonSerdes.StopLinePassageNotification().deserializer());
            
            inputTopic.pipeInput(stopLinePassageEventKey, stopLinePassageEvent2);
            inputTopic.pipeInput(stopLinePassageEventKey, stopLinePassageEvent);

            List<KeyValue<String, StopLinePassageNotification>> notificationResults = outputTopic.readKeyValuesToList();
            
            assertEquals(notificationResults.size(),1);

            StopLinePassageNotification output = notificationResults.get(0).value;

            List<StopLinePassageAssessmentGroup> groups = output.getAssessment().getStopLinePassageAssessmentGroup();

            
            assertEquals(groups.size(), 1);
            
            StopLinePassageAssessmentGroup group = groups.get(0);
            assertEquals(group.getGreenEvents(), 1);
            assertEquals(group.getRedEvents(), 1);
            assertEquals(group.getYellowEvents(), 0);
            assertEquals(group.getDarkEvents(), 0);
            assertEquals(group.getSignalGroup(), 6);


            assertEquals(output.getNotificationHeading(), "Stop Line Passage Notification");
            assertEquals(output.getNotificationText(), "Stop Line Passage Notification, percent of passage events on red: 50% for signal group: 6 exceeds maximum allowable percent.");
            assertEquals(output.getNotificationType(), "StopLinePassageNotification");

        }
    }
}