package us.dot.its.jpo.conflictmonitor.monitor.assessments;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage_assessment.StopLinePassageAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop_assessment.StopLineStopAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLineStopAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.StopLinePassageNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.StopLineStopNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.StopLinePassageAssessmentTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.StopLineStopAssessmentTopology;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class StopLineStopAssessmentTopologyTest {
    String kafkaTopicStopLineStopEvent = "topic.CmStopLineStopEvent";
    String kafkaTopicStopLineStopAssessment = "topic.CmStopLineStopAssessment";
    String kafkaTopicStopLineStopNotification = "topic.CmStopLineStopNotification";
    String stopLineStopEventKey = "12109";
    String stopLineStopEvent = "{\"eventGeneratedAt\":1701281898834,\"eventType\":\"StopLineStop\",\"intersectionID\":12101,\"roadRegulatorID\":-1,\"source\":\"{ rsuId='10.11.81.28', intersectionId='12101', region='0'}\",\"ingressLane\":12,\"egressLane\":29,\"connectionID\":4,\"initialEventState\":\"STOP_AND_REMAIN\",\"initialTimestamp\":1701281879858,\"finalEventState\":\"STOP_AND_REMAIN\",\"finalTimestamp\":1701281882642,\"vehicleID\":\"61899505\",\"latitude\":39.63870516395606,\"longitude\":-105.08191242814065,\"heading\":90.0,\"signalGroup\":4,\"timeStoppedDuringRed\":2.77,\"timeStoppedDuringYellow\":0.0,\"timeStoppedDuringGreen\":3.00,\"key\":\"-1_12101_61899505\"}";
    
    @Test
    public void testTopology() {
        StopLineStopAssessmentTopology assessment = new StopLineStopAssessmentTopology();
        StopLineStopAssessmentParameters parameters = new StopLineStopAssessmentParameters();
        parameters.setDebug(false);
        parameters.setStopLineStopAssessmentOutputTopicName(kafkaTopicStopLineStopAssessment);
        parameters.setStopLineStopEventTopicName(kafkaTopicStopLineStopEvent);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
        parameters.setLookBackPeriodDays(1);
        parameters.setStopLineStopNotificationOutputTopicName(kafkaTopicStopLineStopNotification);
        parameters.setMinimumEventsToNotify(2);
        

        
        assessment.setParameters(parameters);


        Topology topology = assessment.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            TestInputTopic<String, String> inputTopic = driver.createInputTopic(
                kafkaTopicStopLineStopEvent, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, StopLineStopAssessment> outputTopic = driver.createOutputTopic(
                kafkaTopicStopLineStopAssessment, 
                Serdes.String().deserializer(), 
                JsonSerdes.StopLineStopAssessment().deserializer());
            
            inputTopic.pipeInput(stopLineStopEventKey, stopLineStopEvent);
            inputTopic.pipeInput(stopLineStopEventKey, stopLineStopEvent);

            List<KeyValue<String, StopLineStopAssessment>> assessmentResults = outputTopic.readKeyValuesToList();
            
            assertEquals(assessmentResults.size(),2);

            StopLineStopAssessment output = assessmentResults.get(1).value;

            List<StopLineStopAssessmentGroup> groups = output.getStopLineStopAssessmentGroup();

            
            assertEquals(groups.size(), 1);
            
            StopLineStopAssessmentGroup group = groups.get(0);
            assertEquals(group.getTimeStoppedOnGreen(), 6.00);
            assertEquals(group.getTimeStoppedOnRed(), 2.77 * 2);
            assertEquals(group.getTimeStoppedOnYellow(), 0);
            assertEquals(group.getTimeStoppedOnDark(), 0);
            assertEquals(group.getSignalGroup(), 4);
        }
    }


    @Test
    public void testNotification() {
        StopLineStopAssessmentTopology assessment = new StopLineStopAssessmentTopology();
        StopLineStopAssessmentParameters parameters = new StopLineStopAssessmentParameters();
        parameters.setDebug(false);
        parameters.setStopLineStopAssessmentOutputTopicName(kafkaTopicStopLineStopAssessment);
        parameters.setStopLineStopEventTopicName(kafkaTopicStopLineStopEvent);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
        parameters.setLookBackPeriodDays(1);
        parameters.setStopLineStopNotificationOutputTopicName(kafkaTopicStopLineStopNotification);
        parameters.setMinimumEventsToNotify(2);
        parameters.setGreenLightPercentToNotify(0.1);

        
        assessment.setParameters(parameters);


        Topology topology = assessment.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            TestInputTopic<String, String> inputTopic = driver.createInputTopic(
                kafkaTopicStopLineStopEvent, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, StopLineStopNotification> outputTopic = driver.createOutputTopic(
                kafkaTopicStopLineStopNotification, 
                Serdes.String().deserializer(),
                JsonSerdes.StopLineStopNotification().deserializer());
            
            inputTopic.pipeInput(stopLineStopEventKey, stopLineStopEvent);
            inputTopic.pipeInput(stopLineStopEventKey, stopLineStopEvent);

            List<KeyValue<String, StopLineStopNotification>> notificationResults = outputTopic.readKeyValuesToList();
            
            assertEquals(notificationResults.size(),1);

            StopLineStopNotification output = notificationResults.get(0).value;

            List<StopLineStopAssessmentGroup> groups = output.getAssessment().getStopLineStopAssessmentGroup();

            
            assertEquals(groups.size(), 1);
            
            StopLineStopAssessmentGroup group = groups.get(0);
            assertEquals(group.getTimeStoppedOnGreen(), 6.00);
            assertEquals(group.getTimeStoppedOnRed(), 2.77 * 2);
            assertEquals(group.getTimeStoppedOnYellow(), 0);
            assertEquals(group.getTimeStoppedOnDark(), 0);
            assertEquals(group.getSignalGroup(), 4);


            assertEquals(output.getNotificationHeading(), "Stop Line Stop Notification");
            assertEquals(output.getNotificationText(), "Stop Line Stop Notification, percent time stopped on green: 52% for signal group: 4 exceeds maximum allowable percent.");
            assertEquals(output.getNotificationType(), "StopLineStopNotification");
        }
    }
}