package deduplicator;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import us.dot.its.jpo.deduplicator.DeduplicatorProperties;
import us.dot.its.jpo.deduplicator.deduplicator.topologies.OdeRawEncodedTimDeduplicatorTopology;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class OdeRawEncodedTimDeduplicatorTopologyTest {

    String inputTopic = "topic.OdeRawEncodedTIMJson";
    String outputTopic = "topic.DeduplicatedOdeRawEncodedTIMJson";

    ObjectMapper objectMapper = new ObjectMapper();

    // Original Message
    String inputTim1 = "{ \"metadata\": { \"securityResultCode\": \"success\", \"recordGeneratedBy\": \"RSU\", \"schemaVersion\": 6, \"payloadType\": \"us.dot.its.jpo.ode.model.OdeAsn1Payload\", \"serialId\": { \"recordId\": 0, \"serialNumber\": 0, \"streamId\": \"5f470323-7770-4810-b225-8c45aa672103\", \"bundleSize\": 1, \"bundleId\": 0 }, \"sanitized\": false, \"recordType\": \"timMsg\", \"maxDurationTime\": 0, \"odeReceivedAt\": \"2024-07-22T23:23:29.553Z\", \"originIp\": \"10.16.28.53\" }, \"payload\": { \"data\": { \"bytes\": \"001F63701409FF38D05CD47AF567A4570F775D9B0301C269D16DD9656F9637FFF93F421D3B001EA007F99937E1CF5AD1BB0BF4A9D5BEC5BB25CC5B2E64E173162DA00000000269D16DD9656F9631388C100021000EBF7272441F8CFDED60004008027BBAECD8A1A81EF1C153853DD08B394DDCE85F7F4F2222BE087C98000801004F775D9B002F378A81FD1358540F893502C0B711D815FF7883E0A9AACF804536FC9E2A39A67D4289155859ABA8ACBD4997855D345BA429991568E1CA702BA1D8ADFF4805456C0A46862B7F41BE614A122DFB8B0FA019FC52FC8AFF62A7DA3D3F1C9A00BC220AA6B0DC20571A23C142A8C0368F14D181D9B8E2FA859714531606FF8197DC2D81469C97540A33D8D25850D7CA7E82916C1F82142D3A8A20A4884311C320C8EB2290AD1BAC0C84856E8A1B1D760C51BE458B8189591FF0C64E68D0004008027BBAECD8000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\" }, \"dataType\": \"us.dot.its.jpo.ode.model.OdeHexByteArray\" }}";
    
    // Shifted Forward .1 seconds - Should be deduplicated
    String inputTim2 = "{ \"metadata\": { \"securityResultCode\": \"success\", \"recordGeneratedBy\": \"RSU\", \"schemaVersion\": 6, \"payloadType\": \"us.dot.its.jpo.ode.model.OdeAsn1Payload\", \"serialId\": { \"recordId\": 0, \"serialNumber\": 0, \"streamId\": \"5f470323-7770-4810-b225-8c45aa672103\", \"bundleSize\": 1, \"bundleId\": 0 }, \"sanitized\": false, \"recordType\": \"timMsg\", \"maxDurationTime\": 0, \"odeReceivedAt\": \"2024-07-22T23:23:29.653Z\", \"originIp\": \"10.16.28.53\" }, \"payload\": { \"data\": { \"bytes\": \"001F63701409FF38D05CD47AF567A4570F775D9B0301C269D16DD9656F9637FFF93F421D3B001EA007F99937E1CF5AD1BB0BF4A9D5BEC5BB25CC5B2E64E173162DA00000000269D16DD9656F9631388C100021000EBF7272441F8CFDED60004008027BBAECD8A1A81EF1C153853DD08B394DDCE85F7F4F2222BE087C98000801004F775D9B002F378A81FD1358540F893502C0B711D815FF7883E0A9AACF804536FC9E2A39A67D4289155859ABA8ACBD4997855D345BA429991568E1CA702BA1D8ADFF4805456C0A46862B7F41BE614A122DFB8B0FA019FC52FC8AFF62A7DA3D3F1C9A00BC220AA6B0DC20571A23C142A8C0368F14D181D9B8E2FA859714531606FF8197DC2D81469C97540A33D8D25850D7CA7E82916C1F82142D3A8A20A4884311C320C8EB2290AD1BAC0C84856E8A1B1D760C51BE458B8189591FF0C64E68D0004008027BBAECD8000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\" }, \"dataType\": \"us.dot.its.jpo.ode.model.OdeHexByteArray\" }}";
    
    // Shifted Forward 1 hour Should be allowed to pass through
    String inputTim3 = "{ \"metadata\": { \"securityResultCode\": \"success\", \"recordGeneratedBy\": \"RSU\", \"schemaVersion\": 6, \"payloadType\": \"us.dot.its.jpo.ode.model.OdeAsn1Payload\", \"serialId\": { \"recordId\": 0, \"serialNumber\": 0, \"streamId\": \"5f470323-7770-4810-b225-8c45aa672103\", \"bundleSize\": 1, \"bundleId\": 0 }, \"sanitized\": false, \"recordType\": \"timMsg\", \"maxDurationTime\": 0, \"odeReceivedAt\": \"2024-07-23T00:23:29.653Z\", \"originIp\": \"10.16.28.53\" }, \"payload\": { \"data\": { \"bytes\": \"001F63701409FF38D05CD47AF567A4570F775D9B0301C269D16DD9656F9637FFF93F421D3B001EA007F99937E1CF5AD1BB0BF4A9D5BEC5BB25CC5B2E64E173162DA00000000269D16DD9656F9631388C100021000EBF7272441F8CFDED60004008027BBAECD8A1A81EF1C153853DD08B394DDCE85F7F4F2222BE087C98000801004F775D9B002F378A81FD1358540F893502C0B711D815FF7883E0A9AACF804536FC9E2A39A67D4289155859ABA8ACBD4997855D345BA429991568E1CA702BA1D8ADFF4805456C0A46862B7F41BE614A122DFB8B0FA019FC52FC8AFF62A7DA3D3F1C9A00BC220AA6B0DC20571A23C142A8C0368F14D181D9B8E2FA859714531606FF8197DC2D81469C97540A33D8D25850D7CA7E82916C1F82142D3A8A20A4884311C320C8EB2290AD1BAC0C84856E8A1B1D760C51BE458B8189591FF0C64E68D0004008027BBAECD8000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\" }, \"dataType\": \"us.dot.its.jpo.ode.model.OdeHexByteArray\" }}";
    
    // Has a different payload ID. Should be allowed through
    String inputTim4 = "{ \"metadata\": { \"securityResultCode\": \"success\", \"recordGeneratedBy\": \"RSU\", \"schemaVersion\": 6, \"payloadType\": \"us.dot.its.jpo.ode.model.OdeAsn1Payload\", \"serialId\": { \"recordId\": 0, \"serialNumber\": 0, \"streamId\": \"5f470323-7770-4810-b225-8c45aa672103\", \"bundleSize\": 1, \"bundleId\": 0 }, \"sanitized\": false, \"recordType\": \"timMsg\", \"maxDurationTime\": 0, \"odeReceivedAt\": \"2024-07-22T23:23:29.553Z\", \"originIp\": \"10.16.28.53\" }, \"payload\": { \"data\": { \"bytes\": \"001F63701409FF38D05CD47AF567A4570F775D9B0301C269D16DD9656F9637FFF93F421D3B001EA007F99937E1CF5AD1BB0BF4A9D5BEC5BB25CC5B2E64E173162DA00000000269D16DD9656F9631388C100021000EBF7272441F8CFDED60004008027BBAECD8A1A81EF1C153853DD08B394DDCE85F7F4F2222BE087C98000801004F775D9B002F378A81FD1358540F893502C0B711D815FF7883E0A9AACF804536FC9E2A39A67D4289155859ABA8ACBD4997855D345BA429991568E1CA702BA1D8ADFF4805456C0A46862B7F41BE614A122DFB8B0FA019FC52FC8AFF62A7DA3D3F1C9A00BC220AA6B0DC20571A23C142A8C0368F14D181D9B8E2FA859714531606FF8197DC2D81469C97540A33D8D25850D7CA7E82916C1F82142D3A8A20A4884311C320C8EB2290AD1BAC0C84856E8A1B1D760C51BE458B8189591FF0C64E68D0004008027BBAECD9000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\" }, \"dataType\": \"us.dot.its.jpo.ode.model.OdeHexByteArray\" }}";

    @Autowired
    DeduplicatorProperties props;

    @Test
    public void testTopology() {

        props = new DeduplicatorProperties();
        props.setKafkaTopicOdeRawEncodedTimJson(inputTopic);
        props.setKafkaTopicDeduplicatedOdeRawEncodedTimJson(outputTopic);

        OdeRawEncodedTimDeduplicatorTopology TimDeduplicatorTopology = new OdeRawEncodedTimDeduplicatorTopology(props, null);

        Topology topology = TimDeduplicatorTopology.buildTopology();
        objectMapper.registerModule(new JavaTimeModule());

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<Void, String> inputTimData = driver.createInputTopic(
                inputTopic, 
                Serdes.Void().serializer(), 
                Serdes.String().serializer());


            TestOutputTopic<String, String> outputTimData = driver.createOutputTopic(
                outputTopic, 
                Serdes.String().deserializer(), 
                Serdes.String().deserializer());

            inputTimData.pipeInput(null, inputTim1);
            inputTimData.pipeInput(null, inputTim2);
            inputTimData.pipeInput(null, inputTim3);
            inputTimData.pipeInput(null, inputTim4);

            List<KeyValue<String, String>> timDeduplicatedResults = outputTimData.readKeyValuesToList();

            // validate that only 3 messages make it through
            assertEquals(3, timDeduplicatedResults.size());
            inputTim1 = inputTim1.strip();
            
            assertEquals(inputTim1.replace(" ", ""), timDeduplicatedResults.get(0).value.replace(" ", ""));
            assertEquals(inputTim3.replace(" ", ""), timDeduplicatedResults.get(1).value.replace(" ", ""));
            assertEquals(inputTim4.replace(" ", ""), timDeduplicatedResults.get(2).value.replace(" ", ""));
        
        }catch(Exception e){
            e.printStackTrace(); 
        }
    }
}
