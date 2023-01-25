package us.dot.its.jpo.conflictmonitor.monitor.assessments;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel_assessment.ConnectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.ConnectionOfTravelAssessmentTopology;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.LaneDirectionOfTravelAssessmentTopology;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class ConnectionOfTravelAssessmentTopologyTest {
    String kafkaTopicConnectionOfTravelEvent = "topic.CmConnectionOfTravelEvent";
    String kafkaTopicConnectionOfTravelAssessment = "topic.CmConnectionOfTravelAssessment";
    String ConnectionOfTravelEventKey = "12109";
    String ConnectionOfTravelEvent = "{\"eventGeneratedAt\":1673914223688,\"eventType\":\"ConnectionOfTravel\",\"timestamp\":0,\"roadRegulatorId\":-1,\"intersectionId\":12109,\"ingressLaneId\":12,\"egressLaneId\":5,\"connectionId\":1}";
    @Test
    public void testTopology() {
        ConnectionOfTravelAssessmentTopology assessment = new ConnectionOfTravelAssessmentTopology();
        ConnectionOfTravelAssessmentParameters parameters = new ConnectionOfTravelAssessmentParameters();
        parameters.setDebug(false);
        parameters.setConnectionOfTravelEventTopicName(kafkaTopicConnectionOfTravelEvent);
        parameters.setConnectionOfTravelAssessmentOutputTopicName(kafkaTopicConnectionOfTravelAssessment);
        parameters.setLookBackPeriodDays(1);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
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

            ConnectionOfTravelAssessment output = assessmentResults.get(0).value;
            
            List<ConnectionOfTravelAssessmentGroup> groups = output.getConnectionOfTravelAssessment();
            
            assertEquals(groups.size(), 1);

            ConnectionOfTravelAssessmentGroup group = groups.get(0);
            assertEquals(1,group.getConnectionID());
            assertEquals(5, group.getEgressLaneID());
            assertEquals(12, group.getIngressLaneID());
            assertEquals(1,group.getEventCount());
        }
    }
}