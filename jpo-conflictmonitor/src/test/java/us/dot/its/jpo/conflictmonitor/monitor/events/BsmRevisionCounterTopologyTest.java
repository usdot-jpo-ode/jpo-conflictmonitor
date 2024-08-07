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

import us.dot.its.jpo.conflictmonitor.monitor.topologies.BsmRevisionCounterTopology;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_revision_counter.BsmRevisionCounterParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.BsmRevisionCounterEvent;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class BsmRevisionCounterTopologyTest {

    String inputTopic = "topic.CmBsmJsonRepartition";
    String outputTopic = "topic.CmBsmRevisionCounterEvents";


    TypeReference<OdeBsmData> typeReference = new TypeReference<>(){};
    ObjectMapper objectMapper = new ObjectMapper();

    String inputBsm1 = "{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"\",\"longitude\":\"\",\"elevation\":\"\",\"speed\":\"\",\"heading\":\"\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"849c3ac6-4747-426f-ae5e-28ac8531b32c\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2023-05-03T16:25:05.988Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"10.11.81.12\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":12,\"id\":\"48C45782\",\"secMark\":5988,\"position\":{\"latitude\":39.58785476153264,\"longitude\":-105.09073473997064,\"elevation\":1691.9},\"accelSet\":{\"accelLong\":-0.05,\"accelYaw\":-0.65},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":0},\"transmission\":\"UNAVAILABLE\",\"speed\":24.74,\"heading\":347.3,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"off\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":180,\"length\":480}},\"partII\":[{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":0.6,\"latOffset\":-0.0001604,\"lonOffset\":0.0000387,\"timeOffset\":0.8},{\"elevationOffset\":4.3,\"latOffset\":-0.0011603,\"lonOffset\":0.000231,\"timeOffset\":5.6},{\"elevationOffset\":9.1,\"latOffset\":-0.0023425,\"lonOffset\":0.0003834,\"timeOffset\":11}]},\"pathPrediction\":{\"confidence\":80,\"radiusOfCurve\":-1622.5}}}]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";
    String inputBsm2 = "{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"\",\"longitude\":\"\",\"elevation\":\"\",\"speed\":\"\",\"heading\":\"\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"849c3ac6-4747-426f-ae5e-28ac8531b32c\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2023-05-03T16:25:05.988Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"10.11.81.12\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":13,\"id\":\"48C45782\",\"secMark\":5988,\"position\":{\"latitude\":39.58785476153264,\"longitude\":-105.09073473997064,\"elevation\":1691.9},\"accelSet\":{\"accelLong\":-0.05,\"accelYaw\":-0.65},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":0},\"transmission\":\"UNAVAILABLE\",\"speed\":24.74,\"heading\":347.3,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"off\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":180,\"length\":480}},\"partII\":[{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":0.6,\"latOffset\":-0.0001604,\"lonOffset\":0.0000387,\"timeOffset\":0.8},{\"elevationOffset\":4.3,\"latOffset\":-0.0011603,\"lonOffset\":0.000231,\"timeOffset\":5.6},{\"elevationOffset\":9.1,\"latOffset\":-0.0023425,\"lonOffset\":0.0003834,\"timeOffset\":11}]},\"pathPrediction\":{\"confidence\":80,\"radiusOfCurve\":-1622.5}}}]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";
    String inputBsm3 = "{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"\",\"longitude\":\"\",\"elevation\":\"\",\"speed\":\"\",\"heading\":\"\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"849c3ac6-4747-426f-ae5e-28ac8531b32c\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2023-05-03T16:25:05.988Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"10.11.81.12\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":14,\"id\":\"48C45782\",\"secMark\":5987,\"position\":{\"latitude\":39.58785476153264,\"longitude\":-105.09073473997064,\"elevation\":1691.9},\"accelSet\":{\"accelLong\":-0.05,\"accelYaw\":-0.65},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":0},\"transmission\":\"UNAVAILABLE\",\"speed\":24.74,\"heading\":347.3,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"off\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":180,\"length\":480}},\"partII\":[{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":0.6,\"latOffset\":-0.0001604,\"lonOffset\":0.0000387,\"timeOffset\":0.8},{\"elevationOffset\":4.3,\"latOffset\":-0.0011603,\"lonOffset\":0.000231,\"timeOffset\":5.6},{\"elevationOffset\":9.1,\"latOffset\":-0.0023425,\"lonOffset\":0.0003834,\"timeOffset\":11}]},\"pathPrediction\":{\"confidence\":80,\"radiusOfCurve\":-1622.5}}}]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";
    String inputBsm4 = "{\"metadata\":{\"bsmSource\":\"RV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"\",\"longitude\":\"\",\"elevation\":\"\",\"speed\":\"\",\"heading\":\"\"},\"rxSource\":\"RV\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"849c3ac6-4747-426f-ae5e-28ac8531b32c\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2023-05-03T16:25:05.988Z\",\"schemaVersion\":7,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"10.11.81.12\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":14,\"id\":\"48C45782\",\"secMark\":5988,\"position\":{\"latitude\":39.58785476153264,\"longitude\":-105.09073473997064,\"elevation\":1691.9},\"accelSet\":{\"accelLong\":-0.05,\"accelYaw\":-0.65},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":0},\"transmission\":\"UNAVAILABLE\",\"speed\":24.74,\"heading\":347.3,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"off\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":180,\"length\":480}},\"partII\":[{\"id\":\"VehicleSafetyExtensions\",\"value\":{\"pathHistory\":{\"crumbData\":[{\"elevationOffset\":0.6,\"latOffset\":-0.0001604,\"lonOffset\":0.0000387,\"timeOffset\":0.8},{\"elevationOffset\":4.3,\"latOffset\":-0.0011603,\"lonOffset\":0.000231,\"timeOffset\":5.6},{\"elevationOffset\":9.1,\"latOffset\":-0.0023425,\"lonOffset\":0.0003834,\"timeOffset\":11}]},\"pathPrediction\":{\"confidence\":80,\"radiusOfCurve\":-1622.5}}}]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";

    String key = "{\"rsuId\":\"10.11.81.12\",\"intersectionId\":12109,\"region\":-1}";

    @Test
    public void testTopology() {

        BsmRevisionCounterTopology bsmRevisionCounterTopology = new BsmRevisionCounterTopology();
        BsmRevisionCounterParameters parameters = new BsmRevisionCounterParameters();
        parameters.setBsmInputTopicName(inputTopic);
        parameters.setBsmRevisionEventOutputTopicName(outputTopic);
        
        bsmRevisionCounterTopology.setParameters(parameters);

        Topology topology = bsmRevisionCounterTopology.buildTopology();
        objectMapper.registerModule(new JavaTimeModule());

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<String, String> inputBsmData = driver.createInputTopic(
                inputTopic, 
                Serdes.String().serializer(), 
                Serdes.String().serializer());

            TestOutputTopic<String, BsmRevisionCounterEvent> outputRevisionCounterEvents = driver.createOutputTopic(
                outputTopic, 
                Serdes.String().deserializer(), 
                us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes.BsmRevisionCounterEvent().deserializer());

            inputBsmData.pipeInput(key, inputBsm1); // a bsm
            inputBsmData.pipeInput(key, inputBsm2); // the same bsm, with msgCount incremented
            inputBsmData.pipeInput(key, inputBsm3); // a changed bsm, with msgCount incremented
            inputBsmData.pipeInput(key, inputBsm4); // a changed bsm, without msgCount incremented
            inputBsmData.pipeInput(key, inputBsm4); // bsm 4 again

            List<KeyValue<String, BsmRevisionCounterEvent>> bsmRevisionCounterEvents = outputRevisionCounterEvents.readKeyValuesToList();

            // validate that just 1 event is returned, for the 3-4 bsm change without a revision change
            assertEquals(1, bsmRevisionCounterEvents.size());

            OdeBsmData bsm3 = objectMapper.readValue(inputBsm3, typeReference);
            OdeBsmData bsm4 = objectMapper.readValue(inputBsm4, typeReference);
            J2735Bsm eventPreviousBsmPayload = (J2735Bsm) bsmRevisionCounterEvents.get(0).value.getPreviousBsm().getPayload().getData();
            J2735Bsm eventNewBsmPayload = (J2735Bsm) bsmRevisionCounterEvents.get(0).value.getNewBsm().getPayload().getData();

//            assertEquals(bsm3.getOdeReceivedAt(), bsmRevisionCounterEvents.get(0).value.getPreviousBsm().getOdeReceivedAt());
//            assertEquals(bsm4.getOdeReceivedAt(), bsmRevisionCounterEvents.get(0).value.getNewBsm().getOdeReceivedAt());
            assertEquals(eventPreviousBsmPayload.getCoreData().getMsgCnt(), eventNewBsmPayload.getCoreData().getMsgCnt());
            
            int hashBsm3 = bsm3.hashCode();
            int hashBsm4 = bsm4.hashCode();    
            assertNotEquals(hashBsm3, hashBsm4);
        

           
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
