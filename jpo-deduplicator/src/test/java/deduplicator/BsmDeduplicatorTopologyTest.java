package deduplicator;



import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.topologies.BsmDeduplicatorTopology;
import us.dot.its.jpo.ode.model.OdeBsmData;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class BsmDeduplicatorTopologyTest {

    String inputTopic = "topic.OdeBsmJson";
    String outputTopic = "topic.DeduplicatedOdeBsmJson";
    ObjectMapper objectMapper;

    String inputBsm1 = "{\"metadata\":{\"bsmSource\":\"EV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"unavailable\",\"longitude\":\"unavailable\",\"elevation\":\"unavailable\",\"speed\":\"unavailable\",\"heading\":\"unavailable\"},\"rxSource\":\"RSU\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"67d5673c-206a-456e-803d-2fe4b6f89a22\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-06-28T22:47:26.205Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"172.18.0.1\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":37,\"id\":\"31325433\",\"secMark\":25399,\"position\":{\"latitude\":40.5659938,\"longitude\":-105.0317754,\"elevation\":1440.9},\"accelSet\":{\"accelLat\":0.00,\"accelLong\":0.27,\"accelVert\":0.00,\"accelYaw\":0.00},\"accuracy\":{\"semiMajor\":9.30,\"semiMinor\":12.05},\"transmission\":\"UNAVAILABLE\",\"speed\":0.28,\"heading\":313.2500,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":190,\"length\":570}},\"partII\":[]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";
    
    // Same as BSM 1 - No Message should be generated
    String inputBsm2 = "{\"metadata\":{\"bsmSource\":\"EV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"unavailable\",\"longitude\":\"unavailable\",\"elevation\":\"unavailable\",\"speed\":\"unavailable\",\"heading\":\"unavailable\"},\"rxSource\":\"RSU\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"67d5673c-206a-456e-803d-2fe4b6f89a22\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-06-28T22:47:26.205Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"172.18.0.1\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":37,\"id\":\"31325433\",\"secMark\":25399,\"position\":{\"latitude\":40.5659938,\"longitude\":-105.0317754,\"elevation\":1440.9},\"accelSet\":{\"accelLat\":0.00,\"accelLong\":0.27,\"accelVert\":0.00,\"accelYaw\":0.00},\"accuracy\":{\"semiMajor\":9.30,\"semiMinor\":12.05},\"transmission\":\"UNAVAILABLE\",\"speed\":0.28,\"heading\":313.2500,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":190,\"length\":570}},\"partII\":[]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";
    
    // Increase Time from Bsm 1
    String inputBsm3 = "{\"metadata\":{\"bsmSource\":\"EV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"unavailable\",\"longitude\":\"unavailable\",\"elevation\":\"unavailable\",\"speed\":\"unavailable\",\"heading\":\"unavailable\"},\"rxSource\":\"RSU\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"67d5673c-206a-456e-803d-2fe4b6f89a22\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-06-28T22:47:26.205Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"172.18.0.1\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":37,\"id\":\"31325433\",\"secMark\":25399,\"position\":{\"latitude\":40.5659938,\"longitude\":-105.0317754,\"elevation\":1440.9},\"accelSet\":{\"accelLat\":0.00,\"accelLong\":0.27,\"accelVert\":0.00,\"accelYaw\":0.00},\"accuracy\":{\"semiMajor\":9.30,\"semiMinor\":12.05},\"transmission\":\"UNAVAILABLE\",\"speed\":0.28,\"heading\":313.2500,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":190,\"length\":570}},\"partII\":[]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";
    
    // Vehicle Speed not 0
    String inputBsm4 = "{\"metadata\":{\"bsmSource\":\"EV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"unavailable\",\"longitude\":\"unavailable\",\"elevation\":\"unavailable\",\"speed\":\"unavailable\",\"heading\":\"unavailable\"},\"rxSource\":\"RSU\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"67d5673c-206a-456e-803d-2fe4b6f89a22\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-06-28T22:47:26.205Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"172.18.0.1\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":37,\"id\":\"31325433\",\"secMark\":25399,\"position\":{\"latitude\":40.5659938,\"longitude\":-105.0317754,\"elevation\":1440.9},\"accelSet\":{\"accelLat\":0.00,\"accelLong\":0.27,\"accelVert\":0.00,\"accelYaw\":0.00},\"accuracy\":{\"semiMajor\":9.30,\"semiMinor\":12.05},\"transmission\":\"UNAVAILABLE\",\"speed\":0.28,\"heading\":313.2500,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":190,\"length\":570}},\"partII\":[]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";

    // Vehicle Position has changed 
    String inputBsm5 = "{\"metadata\":{\"bsmSource\":\"EV\",\"logFileName\":\"\",\"recordType\":\"bsmTx\",\"securityResultCode\":\"success\",\"receivedMessageDetails\":{\"locationData\":{\"latitude\":\"unavailable\",\"longitude\":\"unavailable\",\"elevation\":\"unavailable\",\"speed\":\"unavailable\",\"heading\":\"unavailable\"},\"rxSource\":\"RSU\"},\"payloadType\":\"us.dot.its.jpo.ode.model.OdeBsmPayload\",\"serialId\":{\"streamId\":\"67d5673c-206a-456e-803d-2fe4b6f89a22\",\"bundleSize\":1,\"bundleId\":0,\"recordId\":0,\"serialNumber\":0},\"odeReceivedAt\":\"2024-06-28T22:47:26.205Z\",\"schemaVersion\":6,\"maxDurationTime\":0,\"recordGeneratedAt\":\"\",\"recordGeneratedBy\":\"OBU\",\"sanitized\":false,\"odePacketID\":\"\",\"odeTimStartDateTime\":\"\",\"originIp\":\"172.18.0.1\"},\"payload\":{\"data\":{\"coreData\":{\"msgCnt\":37,\"id\":\"31325433\",\"secMark\":25399,\"position\":{\"latitude\":40.5659938,\"longitude\":-105.0317754,\"elevation\":1440.9},\"accelSet\":{\"accelLat\":0.00,\"accelLong\":0.27,\"accelVert\":0.00,\"accelYaw\":0.00},\"accuracy\":{\"semiMajor\":9.30,\"semiMinor\":12.05},\"transmission\":\"UNAVAILABLE\",\"speed\":0.28,\"heading\":313.2500,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"unavailable\",\"abs\":\"unavailable\",\"scs\":\"unavailable\",\"brakeBoost\":\"unavailable\",\"auxBrakes\":\"unavailable\"},\"size\":{\"width\":190,\"length\":570}},\"partII\":[]},\"dataType\":\"us.dot.its.jpo.ode.plugin.j2735.J2735Bsm\"}}";
    
    
    @Autowired
    DeduplicatorProperties props;

    

    @Test
    public void testTopology() {

        BsmDeduplicatorTopology bsmDeduplicatorTopology = new BsmDeduplicatorTopology(props);

        Topology topology = bsmDeduplicatorTopology.buildTopology();

        props = new DeduplicatorProperties();
        props.setAlwaysIncludeAtSpeed(1);
        props.setenableOdeBsmDeduplication(true);
        props.setMaximumPositionDelta(1);
        props.setMaximumTimeDelta(10000);
        props.setkafkaTopicOdeBsmJson(inputTopic);
        props.setkafkaTopicDeduplicatedOdeBsmJson(outputTopic);

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<Void, String> inputOdeBsmData = driver.createInputTopic(
                inputTopic, 
                Serdes.Void().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, OdeBsmData> outputOdeBsmData = driver.createOutputTopic(
                outputTopic, 
                Serdes.String().deserializer(), 
                JsonSerdes.OdeBsm().deserializer());

            inputOdeBsmData.pipeInput(null, inputBsm1);
            inputOdeBsmData.pipeInput(null, inputBsm2);
            inputOdeBsmData.pipeInput(null, inputBsm3);
            inputOdeBsmData.pipeInput(null, inputBsm4);

            List<KeyValue<String, OdeBsmData>> bsmDeduplicationResults = outputOdeBsmData.readKeyValuesToList();

            // validate that only 3 messages make it through
            assertEquals(3, bsmDeduplicationResults.size());

            objectMapper = new ObjectMapper();
            OdeBsmData bsm1 = objectMapper.readValue(inputBsm1, OdeBsmData.class);
            OdeBsmData bsm3 = objectMapper.readValue(inputBsm3, OdeBsmData.class);
            OdeBsmData bsm4 = objectMapper.readValue(inputBsm4, OdeBsmData.class);
            OdeBsmData bsm5 = objectMapper.readValue(inputBsm4, OdeBsmData.class);


            assertEquals(bsm1.getMetadata().getOdeReceivedAt(), bsmDeduplicationResults.get(0).value.getMetadata().getOdeReceivedAt());
            assertEquals(bsm3.getMetadata().getOdeReceivedAt(), bsmDeduplicationResults.get(1).value.getMetadata().getOdeReceivedAt());
            assertEquals(bsm4.getMetadata().getOdeReceivedAt(), bsmDeduplicationResults.get(2).value.getMetadata().getOdeReceivedAt());
            assertEquals(bsm4.getMetadata().getOdeReceivedAt(), bsmDeduplicationResults.get(3).value.getMetadata().getOdeReceivedAt());
           
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}