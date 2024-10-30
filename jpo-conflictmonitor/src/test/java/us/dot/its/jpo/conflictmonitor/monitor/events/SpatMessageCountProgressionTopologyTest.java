package us.dot.its.jpo.conflictmonitor.monitor.events;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import us.dot.its.jpo.conflictmonitor.monitor.topologies.SpatMessageCountProgressionTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.spat_revision_counter.SpatMessageCountProgressionParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SpatMessageCountProgressionEvent;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class SpatMessageCountProgressionTopologyTest {

    String inputTopic = "topic.ProcessedSpat";
    String outputTopic = "topic.CmSpatMessageCountProgressionEvents";


    TypeReference<ProcessedSpat> typeReference = new TypeReference<>(){};
    ObjectMapper objectMapper = new ObjectMapper();

    String inputProcessedSpat1 = "{\"messageType\":\"SPAT\",\"odeReceivedAt\":\"2023-05-03T16:25:04.807Z\",\"originIp\":\"10.11.81.12\",\"intersectionId\":12109,\"cti4501Conformant\":false,\"validationMessages\":[{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region\",\"schemaPath\":\"#/$defs/J2735RoadRegulatorID/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"}],\"revision\":0,\"status\":{\"manualControlIsEnabled\":false,\"stopTimeIsActivated\":false,\"failureFlash\":false,\"preemptIsActive\":false,\"signalPriorityIsActive\":false,\"fixedTimeOperation\":false,\"trafficDependentOperation\":false,\"standbyOperation\":false,\"failureMode\":false,\"off\":false,\"recentMAPmessageUpdate\":false,\"recentChangeInMAPassignedLanesIDsUsed\":false,\"noValidMAPisAvailableAtThisTime\":false,\"noValidSPATisAvailableAtThisTime\":false},\"utcTimeStamp\":\"2023-05-03T16:25:04.807Z\",\"states\":[{\"signalGroup\":2,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]},{\"signalGroup\":4,\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:21.1Z\",\"maxEndTime\":\"2023-05-03T16:03:21.1Z\"}}]},{\"signalGroup\":6,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]}]}";
    String inputProcessedSpat2 = "{\"messageType\":\"SPAT\",\"odeReceivedAt\":\"2023-05-03T16:25:04.807Z\",\"originIp\":\"10.11.81.12\",\"intersectionId\":12109,\"cti4501Conformant\":false,\"validationMessages\":[{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region\",\"schemaPath\":\"#/$defs/J2735RoadRegulatorID/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"}],\"revision\":1,\"status\":{\"manualControlIsEnabled\":false,\"stopTimeIsActivated\":false,\"failureFlash\":false,\"preemptIsActive\":false,\"signalPriorityIsActive\":false,\"fixedTimeOperation\":false,\"trafficDependentOperation\":false,\"standbyOperation\":false,\"failureMode\":false,\"off\":false,\"recentMAPmessageUpdate\":false,\"recentChangeInMAPassignedLanesIDsUsed\":false,\"noValidMAPisAvailableAtThisTime\":false,\"noValidSPATisAvailableAtThisTime\":false},\"utcTimeStamp\":\"2023-05-03T16:25:04.807Z\",\"states\":[{\"signalGroup\":2,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]},{\"signalGroup\":4,\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:21.1Z\",\"maxEndTime\":\"2023-05-03T16:03:21.1Z\"}}]},{\"signalGroup\":6,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]}]}";
    String inputProcessedSpat3 = "{\"messageType\":\"SPAT\",\"odeReceivedAt\":\"2023-05-03T16:25:04.807Z\",\"originIp\":\"10.11.81.12\",\"intersectionId\":12109,\"cti4501Conformant\":false,\"validationMessages\":[{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region\",\"schemaPath\":\"#/$defs/J2735RoadRegulatorID/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"}],\"revision\":2,\"status\":{\"manualControlIsEnabled\":false,\"stopTimeIsActivated\":false,\"failureFlash\":false,\"preemptIsActive\":false,\"signalPriorityIsActive\":true,\"fixedTimeOperation\":false,\"trafficDependentOperation\":false,\"standbyOperation\":false,\"failureMode\":false,\"off\":false,\"recentMAPmessageUpdate\":false,\"recentChangeInMAPassignedLanesIDsUsed\":false,\"noValidMAPisAvailableAtThisTime\":false,\"noValidSPATisAvailableAtThisTime\":false},\"utcTimeStamp\":\"2023-05-03T16:25:04.807Z\",\"states\":[{\"signalGroup\":2,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]},{\"signalGroup\":4,\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:21.1Z\",\"maxEndTime\":\"2023-05-03T16:03:21.1Z\"}}]},{\"signalGroup\":6,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]}]}";
    String inputProcessedSpat4 = "{\"messageType\":\"SPAT\",\"odeReceivedAt\":\"2023-05-03T16:25:04.807Z\",\"originIp\":\"10.11.81.12\",\"intersectionId\":12109,\"cti4501Conformant\":false,\"validationMessages\":[{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region\",\"schemaPath\":\"#/$defs/J2735RoadRegulatorID/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"}],\"revision\":2,\"status\":{\"manualControlIsEnabled\":false,\"stopTimeIsActivated\":false,\"failureFlash\":true,\"preemptIsActive\":false,\"signalPriorityIsActive\":true,\"fixedTimeOperation\":false,\"trafficDependentOperation\":false,\"standbyOperation\":false,\"failureMode\":false,\"off\":false,\"recentMAPmessageUpdate\":false,\"recentChangeInMAPassignedLanesIDsUsed\":false,\"noValidMAPisAvailableAtThisTime\":false,\"noValidSPATisAvailableAtThisTime\":false},\"utcTimeStamp\":\"2023-05-03T16:25:04.807Z\",\"states\":[{\"signalGroup\":2,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]},{\"signalGroup\":4,\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:21.1Z\",\"maxEndTime\":\"2023-05-03T16:03:21.1Z\"}}]},{\"signalGroup\":6,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]}]}";


    String key = "{\"rsuId\":\"10.11.81.12\",\"intersectionId\":12109,\"region\":-1}";


    

    @Test
    public void testTopology() {

        SpatMessageCountProgressionTopology spatMessageCountProgressionTopology = new SpatMessageCountProgressionTopology();
        SpatMessageCountProgressionParameters parameters = new SpatMessageCountProgressionParameters();
        parameters.setSpatInputTopicName(inputTopic);
        parameters.setSpatRevisionEventOutputTopicName(outputTopic);
        
        spatMessageCountProgressionTopology.setParameters(parameters);

        Topology topology = spatMessageCountProgressionTopology.buildTopology();
        objectMapper.registerModule(new JavaTimeModule());

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<String, String> inputProcessedSpatData = driver.createInputTopic(
                inputTopic, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());

            TestOutputTopic<String, SpatMessageCountProgressionEvent> outputRevisionCounterEvents = driver.createOutputTopic(
                outputTopic, 
                Serdes.String().deserializer(), 
                us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.SpatMessageCountProgressionEvent().deserializer());

            inputProcessedSpatData.pipeInput(key, inputProcessedSpat1); // a spat
            inputProcessedSpatData.pipeInput(key, inputProcessedSpat2); // the same spat, with revision incremented
            inputProcessedSpatData.pipeInput(key, inputProcessedSpat3); // a changed spat, with revision incremented
            inputProcessedSpatData.pipeInput(key, inputProcessedSpat4); // a changed spat, without revision incremented
            inputProcessedSpatData.pipeInput(key, inputProcessedSpat4); // spat 4 again

            List<KeyValue<String, SpatMessageCountProgressionEvent>> spatMessageCountProgressionEvents = outputRevisionCounterEvents.readKeyValuesToList();

            // validate that just 1 event is returned, for the 3-4 spat change without a revision change
            assertEquals(1, spatMessageCountProgressionEvents.size());

            ProcessedSpat spat3 = objectMapper.readValue(inputProcessedSpat3, typeReference);
            ProcessedSpat spat4 = objectMapper.readValue(inputProcessedSpat4, typeReference);

            assertEquals(spat3.getOdeReceivedAt(), spatMessageCountProgressionEvents.get(0).value.getPreviousSpat().getOdeReceivedAt());
            assertEquals(spat4.getOdeReceivedAt(), spatMessageCountProgressionEvents.get(0).value.getNewSpat().getOdeReceivedAt());
            assertEquals(spatMessageCountProgressionEvents.get(0).value.getPreviousSpat().getRevision(),
            spatMessageCountProgressionEvents.get(0).value.getNewSpat().getRevision());

            int hashSpat3 = spat3.hashCode();
            int hashSpat4 = spat4.hashCode();    
            assertNotEquals(hashSpat3, hashSpat4);
        

           
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
