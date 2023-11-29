package us.dot.its.jpo.conflictmonitor.monitor.assessments;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.ConnectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.Assessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.StopLinePassageAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.AssessmentTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.assessment.AssessmentParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class AssessmentTopologyTest {
    String laneDirectionOfTravelAssessmentTopicName = "topic.CmLaneDirectionOfTravelAssessment";
    String connectionOfTravelAssessmentTopicName = "topic.CmConnectionOfTravelAssessment";
    String signalStateEventAssessmentTopicName = "topic.CmSignalStateEventAssessment";
    String assessmentOutputTopicName = "topic.CmAssessment";


    @Test
    public void testTopology() {

        AssessmentTopology assessmentTopology = new AssessmentTopology();
        AssessmentParameters parameters = new AssessmentParameters();
        parameters.setConnectionOfTravelAssessmentTopicName(connectionOfTravelAssessmentTopicName);
        parameters.setLaneDirectionOfTravelAssessmentTopicName(laneDirectionOfTravelAssessmentTopicName);
        parameters.setSignalStateEventAssessmentTopicName(signalStateEventAssessmentTopicName);
        parameters.setAssessmentOutputTopicName(assessmentOutputTopicName);
        parameters.setDebug(false);
        


        assessmentTopology.setParameters(parameters);

        Topology topology = assessmentTopology.buildTopology();

        ConnectionOfTravelAssessment cotAssessment = new ConnectionOfTravelAssessment();
        LaneDirectionOfTravelAssessment ldotAssessment = new LaneDirectionOfTravelAssessment();
        StopLinePassageAssessment sseaAssessment = new StopLinePassageAssessment();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<String, ConnectionOfTravelAssessment> inputConnectionOfTravel = driver.createInputTopic(
                connectionOfTravelAssessmentTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.ConnectionOfTravelAssessment().serializer());

            inputConnectionOfTravel.pipeInput("12109", cotAssessment);

            TestInputTopic<String, LaneDirectionOfTravelAssessment> inputLaneDirectionOfTravel = driver.createInputTopic(
                laneDirectionOfTravelAssessmentTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.LaneDirectionOfTravelAssessment().serializer());

            inputLaneDirectionOfTravel.pipeInput("12109", ldotAssessment);

            TestInputTopic<String, StopLinePassageAssessment> inputSignalStateEvent = driver.createInputTopic(
                laneDirectionOfTravelAssessmentTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.SignalStateEventAssessment().serializer());

            inputSignalStateEvent.pipeInput("12109", sseaAssessment);

            

            

            

            TestOutputTopic<String, Assessment> outputAssessmentTopic = driver.createOutputTopic(
                assessmentOutputTopicName, 
                Serdes.String().deserializer(), 
                JsonSerdes.Assessment().deserializer());
            

            List<KeyValue<String, Assessment>> assessmentResults = outputAssessmentTopic.readKeyValuesToList();

            
            
            assertEquals(4, assessmentResults.size());
 
            for(KeyValue<String, Assessment> assessmentKeyValue: assessmentResults){
                assertEquals("12109", assessmentKeyValue.key);
                Assessment assessment = assessmentKeyValue.value;
                String type = assessment.getAssessmentType();
                if(type.equals("ConnectionOfTravel")){
                    assertEquals((ConnectionOfTravelAssessment) assessment, cotAssessment);
                }
                else if(type.equals("SignalStateEvent")){
                    assertEquals((StopLinePassageAssessment) assessment, sseaAssessment);
                }
                else if(type.equals("LaneDirectionOfTravel")){
                    assertEquals((LaneDirectionOfTravelAssessment) assessment, ldotAssessment);
                }
                else{
                    assertEquals(1,0);
                }
            }            
        }
        assertEquals(0,0);
    }
}