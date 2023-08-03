package us.dot.its.jpo.conflictmonitor.monitor.assessments;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.SignalStateEventAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.SignalStateEventAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.SignalStateEventAssessmentTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_event_assessment.SignalStateEventAssessmentParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class SignalStateEventAssessmentTopologyTest {
    String kafkaTopicSignalStateEvent = "topic.CmSignalStateEvent";
    String kafkaTopicSignalStateAssessment = "topic.CmSignalStateEventAssessment";
    String SignalStateEventKey = "12109";
    String SignalStateEvent = "{\"eventGeneratedAt\":1673974273330,\"eventType\":\"StopLinePassage\",\"timestamp\":1655493260761,\"roadRegulatorID\":-1,\"ingressLane\":12,\"egressLane\":5,\"connectionID\":1,\"eventState\":\"PROTECTED_MOVEMENT_ALLOWED\",\"vehicleID\":\"E6A99808\",\"latitude\":-105.091055,\"longitude\":-105.091055,\"heading\":169.4,\"speed\":22.64,\"signalGroup\":6}";
    @Test
    public void testTopology() {
        SignalStateEventAssessmentTopology assessment = new SignalStateEventAssessmentTopology();
        SignalStateEventAssessmentParameters parameters = new SignalStateEventAssessmentParameters();
        parameters.setDebug(false);
        parameters.setSignalStateEventAssessmentOutputTopicName(kafkaTopicSignalStateAssessment);
        parameters.setSignalStateEventTopicName(kafkaTopicSignalStateEvent);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
        parameters.setLookBackPeriodDays(1);

        
        assessment.setParameters(parameters);


        Topology topology = assessment.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            TestInputTopic<String, String> inputTopic = driver.createInputTopic(
                kafkaTopicSignalStateEvent, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, SignalStateEventAssessment> outputTopic = driver.createOutputTopic(
                kafkaTopicSignalStateAssessment, 
                Serdes.String().deserializer(), 
                JsonSerdes.SignalStateEventAssessment().deserializer());
            
            inputTopic.pipeInput(SignalStateEventKey, SignalStateEvent);

            List<KeyValue<String, SignalStateEventAssessment>> assessmentResults = outputTopic.readKeyValuesToList();
            
            assertEquals(assessmentResults.size(),1);

            SignalStateEventAssessment output = assessmentResults.get(0).value;

            List<SignalStateEventAssessmentGroup> groups = output.getSignalStateEventAssessmentGroup();

            
            assertEquals(groups.size(), 1);
            
            SignalStateEventAssessmentGroup group = groups.get(0);
            assertEquals(group.getGreenEvents(), 1);
            assertEquals(group.getRedEvents(), 0);
            assertEquals(group.getYellowEvents(), 0);
            assertEquals(group.getRedEvents(), 0);
            assertEquals(group.getDarkEvents(), 0);
            assertEquals(group.getSignalGroup(), 0);
        }
    }
}