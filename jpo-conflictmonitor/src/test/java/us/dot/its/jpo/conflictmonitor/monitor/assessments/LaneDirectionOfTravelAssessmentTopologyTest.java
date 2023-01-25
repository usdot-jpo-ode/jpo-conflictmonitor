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

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentAlgorithmFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel_assessment.LaneDirectionOfTravelAssessmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessment;
import us.dot.its.jpo.conflictmonitor.monitor.models.assessments.LaneDirectionOfTravelAssessmentGroup;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.assessments.LaneDirectionOfTravelAssessmentTopology;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class LaneDirectionOfTravelAssessmentTopologyTest {
    String kafkaTopicLaneDirectionOfTravelEvent = "topic.CmLaneDirectionOfTravelEvent";
    String kafkaTopicLaneDirectionOfTravelAssessment = "topic.CmLaneDirectionOfTravelAssessment";
    String laneDirectionOfTravelEventKey = "12109";
    String laneDirectionOfTravelEvent = "{\"eventGeneratedAt\":1673394387458,\"timestamp\":1655493252811,\"roadRegulatorID\":0,\"intersectionID\":12109,\"laneID\":12,\"laneSegmentNumber\":8,\"laneSegmentInitialLatitude\":39.58972728935065,\"laneSegmentInitialLongitude\":-105.091329041372,\"laneSegmentFinalLatitude\":39.59003379187557,\"laneSegmentFinalLongitude\":-105.09136780827767,\"expectedHeading\":174.41112457086354,\"medianVehicleHeading\":174.25,\"medianDistanceFromCenterline\":96.50633375287359,\"aggregateBSMCount\":12}";

    @Test
    public void testTopology() {
        LaneDirectionOfTravelAssessmentTopology assessment = new LaneDirectionOfTravelAssessmentTopology();
        LaneDirectionOfTravelAssessmentParameters parameters = new LaneDirectionOfTravelAssessmentParameters();
        parameters.setDebug(false);
        parameters.setHeadingToleranceDegrees(20);
        parameters.setLaneDirectionOfTravelEventTopicName(kafkaTopicLaneDirectionOfTravelEvent);
        parameters.setLaneDirectionOfTravelAssessmentOutputTopicName(kafkaTopicLaneDirectionOfTravelAssessment);
        parameters.setLookBackPeriodDays(60);
        parameters.setLookBackPeriodGraceTimeSeconds(30);
        assessment.setParameters(parameters);


        Topology topology = assessment.buildTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            TestInputTopic<String, String> inputTopic = driver.createInputTopic(
                kafkaTopicLaneDirectionOfTravelEvent, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, LaneDirectionOfTravelAssessment> outputTopic = driver.createOutputTopic(
                kafkaTopicLaneDirectionOfTravelAssessment, 
                Serdes.String().deserializer(), 
                JsonSerdes.LaneDirectionOfTravelAssessment().deserializer());
            
            inputTopic.pipeInput(laneDirectionOfTravelEventKey, laneDirectionOfTravelEvent);

            List<KeyValue<String, LaneDirectionOfTravelAssessment>> assessmentResults = outputTopic.readKeyValuesToList();
            
            assertEquals(assessmentResults.size(),1);

            LaneDirectionOfTravelAssessment output = assessmentResults.get(0).value;
            
            assertEquals(output.getRoadRegulatorID(), 0);
            assertEquals(output.getIntersectionID(), 12109);
            
            List<LaneDirectionOfTravelAssessmentGroup> groups = output.getLaneDirectionOfTravelAssessmentGroup();
            assertEquals(groups.size(), 1);
            
            LaneDirectionOfTravelAssessmentGroup group = groups.get(0);
            assertEquals(group.getLaneID(), 12);
            assertEquals(group.getSegmentID(), 8);
            assertEquals(group.getInToleranceEvents(), 1);
            assertEquals(group.getOutOfToleranceEvents(), 0);
            assertEquals(group.getMedianInToleranceHeading(), 174.25);
            assertEquals(group.getMedianInToleranceCenterlineDistance(), 96.50633375287359);
            assertEquals(group.getTolerance(), 20);

           
        }
    }
}