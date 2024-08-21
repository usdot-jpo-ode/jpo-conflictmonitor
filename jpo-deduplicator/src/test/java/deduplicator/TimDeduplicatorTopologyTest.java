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
import us.dot.its.jpo.deduplicator.deduplicator.topologies.TimDeduplicatorTopology;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class TimDeduplicatorTopologyTest {

    String inputTopic = "topic.OdeTimJson";
    String outputTopic = "topic.DeduplicatedOdeTimJson";

    ObjectMapper objectMapper = new ObjectMapper();

    //Original Message
    String inputTim1 = "{\"metadata\":{\"securityResultCode\":\"success\",\"recordGeneratedBy\":\"RSU\",\"schemaVersion\":\"6\",\"odePacketID\":\"\",\"sanitized\":\"false\",\"recordType\":\"timMsg\",\"recordGeneratedAt\":\"\",\"maxDurationTime\":\"0\",\"odeTimStartDateTime\":\"\",\"receivedMessageDetails\":\"\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\":{\"recordId\":\"0\",\"serialNumber\":\"0\",\"streamId\":\"d052115a-5289-4da3-bc9f-12edca1d2c46\",\"bundleSize\":\"1\",\"bundleId\":\"0\"},\"logFileName\":\"\",\"odeReceivedAt\":\"2024-07-23T18:33:17.328Z\",\"originIp\":\"10.16.28.54\"},\"payload\":{\"data\":{\"MessageFrame\":{\"messageId\":\"31\",\"value\":{\"TravelerInformation\":{\"timeStamp\":\"264703\",\"packetID\":\"0350C30EB1A83736D8\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":{\"durationTime\":\"30\",\"regions\":{\"GeographicalPath\":{\"closedPath\":{\"false\":\"\"},\"anchor\":{\"lat\":\"395658598\",\"long\":\"-1050401840\"},\"name\":\"I_CO-470_RSU_172.16.28.116\",\"laneWidth\":\"5000\",\"directionality\":{\"both\":\"\"},\"description\":{\"path\":{\"offset\":{\"ll\":{\"nodes\":{\"NodeLL\":[{\"delta\":{\"node-LL1\":{\"lon\":\"1527\",\"lat\":\"-659\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"5545\",\"lat\":\"-2394\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"9493\",\"lat\":\"-2736\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"7465\",\"lat\":\"-1304\"}}},{\"delta\":{\"node-LL4\":{\"lon\":\"34464\",\"lat\":\"-4324\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"9994\",\"lat\":\"-1119\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"20051\",\"lat\":\"-2246\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"31738\",\"lat\":\"-1775\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"5744\",\"lat\":\"-315\"}}}]}}},\"scale\":\"0\"}},\"id\":{\"id\":\"0\",\"region\":\"0\"},\"direction\":\"0000110000000000\"}},\"startYear\":\"2024\",\"notUsed2\":\"0\",\"msgId\":{\"roadSignID\":{\"viewAngle\":\"1111111111111111\",\"mutcdCode\":{\"warning\":\"\"},\"position\":{\"lat\":\"395658598\",\"long\":\"-1050401840\"}}},\"notUsed3\":\"0\",\"notUsed1\":\"0\",\"priority\":\"5\",\"content\":{\"workZone\":{\"SEQUENCE\":{\"item\":{\"itis\":\"1025\"}}}},\"url\":\"null\",\"notUsed\":\"0\",\"frameType\":{\"advisory\":\"\"},\"startTime\":\"277110\"}},\"msgCnt\":\"1\"}}}},\"dataType\":\"TravelerInformation\"}}";
    
    // Shifted Forward .1 seconds - Should be deduplicated
    String inputTim2 = "{\"metadata\":{\"securityResultCode\":\"success\",\"recordGeneratedBy\":\"RSU\",\"schemaVersion\":\"6\",\"odePacketID\":\"\",\"sanitized\":\"false\",\"recordType\":\"timMsg\",\"recordGeneratedAt\":\"\",\"maxDurationTime\":\"0\",\"odeTimStartDateTime\":\"\",\"receivedMessageDetails\":\"\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\":{\"recordId\":\"0\",\"serialNumber\":\"0\",\"streamId\":\"d052115a-5289-4da3-bc9f-12edca1d2c46\",\"bundleSize\":\"1\",\"bundleId\":\"0\"},\"logFileName\":\"\",\"odeReceivedAt\":\"2024-07-23T18:33:17.428Z\",\"originIp\":\"10.16.28.54\"},\"payload\":{\"data\":{\"MessageFrame\":{\"messageId\":\"31\",\"value\":{\"TravelerInformation\":{\"timeStamp\":\"264703\",\"packetID\":\"0350C30EB1A83736D8\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":{\"durationTime\":\"30\",\"regions\":{\"GeographicalPath\":{\"closedPath\":{\"false\":\"\"},\"anchor\":{\"lat\":\"395658598\",\"long\":\"-1050401840\"},\"name\":\"I_CO-470_RSU_172.16.28.116\",\"laneWidth\":\"5000\",\"directionality\":{\"both\":\"\"},\"description\":{\"path\":{\"offset\":{\"ll\":{\"nodes\":{\"NodeLL\":[{\"delta\":{\"node-LL1\":{\"lon\":\"1527\",\"lat\":\"-659\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"5545\",\"lat\":\"-2394\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"9493\",\"lat\":\"-2736\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"7465\",\"lat\":\"-1304\"}}},{\"delta\":{\"node-LL4\":{\"lon\":\"34464\",\"lat\":\"-4324\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"9994\",\"lat\":\"-1119\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"20051\",\"lat\":\"-2246\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"31738\",\"lat\":\"-1775\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"5744\",\"lat\":\"-315\"}}}]}}},\"scale\":\"0\"}},\"id\":{\"id\":\"0\",\"region\":\"0\"},\"direction\":\"0000110000000000\"}},\"startYear\":\"2024\",\"notUsed2\":\"0\",\"msgId\":{\"roadSignID\":{\"viewAngle\":\"1111111111111111\",\"mutcdCode\":{\"warning\":\"\"},\"position\":{\"lat\":\"395658598\",\"long\":\"-1050401840\"}}},\"notUsed3\":\"0\",\"notUsed1\":\"0\",\"priority\":\"5\",\"content\":{\"workZone\":{\"SEQUENCE\":{\"item\":{\"itis\":\"1025\"}}}},\"url\":\"null\",\"notUsed\":\"0\",\"frameType\":{\"advisory\":\"\"},\"startTime\":\"277110\"}},\"msgCnt\":\"1\"}}}},\"dataType\":\"TravelerInformation\"}}";
    
    // Shifted Forward 1 hour Should be allowed to pass through
    String inputTim3 = "{\"metadata\":{\"securityResultCode\":\"success\",\"recordGeneratedBy\":\"RSU\",\"schemaVersion\":\"6\",\"odePacketID\":\"\",\"sanitized\":\"false\",\"recordType\":\"timMsg\",\"recordGeneratedAt\":\"\",\"maxDurationTime\":\"0\",\"odeTimStartDateTime\":\"\",\"receivedMessageDetails\":\"\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\":{\"recordId\":\"0\",\"serialNumber\":\"0\",\"streamId\":\"d052115a-5289-4da3-bc9f-12edca1d2c46\",\"bundleSize\":\"1\",\"bundleId\":\"0\"},\"logFileName\":\"\",\"odeReceivedAt\":\"2024-07-23T19:33:17.428Z\",\"originIp\":\"10.16.28.54\"},\"payload\":{\"data\":{\"MessageFrame\":{\"messageId\":\"31\",\"value\":{\"TravelerInformation\":{\"timeStamp\":\"264703\",\"packetID\":\"0350C30EB1A83736D8\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":{\"durationTime\":\"30\",\"regions\":{\"GeographicalPath\":{\"closedPath\":{\"false\":\"\"},\"anchor\":{\"lat\":\"395658598\",\"long\":\"-1050401840\"},\"name\":\"I_CO-470_RSU_172.16.28.116\",\"laneWidth\":\"5000\",\"directionality\":{\"both\":\"\"},\"description\":{\"path\":{\"offset\":{\"ll\":{\"nodes\":{\"NodeLL\":[{\"delta\":{\"node-LL1\":{\"lon\":\"1527\",\"lat\":\"-659\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"5545\",\"lat\":\"-2394\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"9493\",\"lat\":\"-2736\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"7465\",\"lat\":\"-1304\"}}},{\"delta\":{\"node-LL4\":{\"lon\":\"34464\",\"lat\":\"-4324\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"9994\",\"lat\":\"-1119\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"20051\",\"lat\":\"-2246\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"31738\",\"lat\":\"-1775\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"5744\",\"lat\":\"-315\"}}}]}}},\"scale\":\"0\"}},\"id\":{\"id\":\"0\",\"region\":\"0\"},\"direction\":\"0000110000000000\"}},\"startYear\":\"2024\",\"notUsed2\":\"0\",\"msgId\":{\"roadSignID\":{\"viewAngle\":\"1111111111111111\",\"mutcdCode\":{\"warning\":\"\"},\"position\":{\"lat\":\"395658598\",\"long\":\"-1050401840\"}}},\"notUsed3\":\"0\",\"notUsed1\":\"0\",\"priority\":\"5\",\"content\":{\"workZone\":{\"SEQUENCE\":{\"item\":{\"itis\":\"1025\"}}}},\"url\":\"null\",\"notUsed\":\"0\",\"frameType\":{\"advisory\":\"\"},\"startTime\":\"277110\"}},\"msgCnt\":\"1\"}}}},\"dataType\":\"TravelerInformation\"}}";
    
    // Has a different payload ID. Should be allowed through
    String inputTim4 = "{\"metadata\":{\"securityResultCode\":\"success\",\"recordGeneratedBy\":\"RSU\",\"schemaVersion\":\"6\",\"odePacketID\":\"\",\"sanitized\":\"false\",\"recordType\":\"timMsg\",\"recordGeneratedAt\":\"\",\"maxDurationTime\":\"0\",\"odeTimStartDateTime\":\"\",\"receivedMessageDetails\":\"\",\"payloadType\":\"us.dot.its.jpo.ode.model.OdeTimPayload\",\"serialId\":{\"recordId\":\"0\",\"serialNumber\":\"0\",\"streamId\":\"d052115a-5289-4da3-bc9f-12edca1d2c46\",\"bundleSize\":\"1\",\"bundleId\":\"0\"},\"logFileName\":\"\",\"odeReceivedAt\":\"2024-07-23T19:34:17.328Z\",\"originIp\":\"10.16.28.54\"},\"payload\":{\"data\":{\"MessageFrame\":{\"messageId\":\"31\",\"value\":{\"TravelerInformation\":{\"timeStamp\":\"264703\",\"packetID\":\"0350C30EB1A83736D9\",\"urlB\":\"null\",\"dataFrames\":{\"TravelerDataFrame\":{\"durationTime\":\"30\",\"regions\":{\"GeographicalPath\":{\"closedPath\":{\"false\":\"\"},\"anchor\":{\"lat\":\"395658598\",\"long\":\"-1050401840\"},\"name\":\"I_CO-470_RSU_172.16.28.116\",\"laneWidth\":\"10000\",\"directionality\":{\"both\":\"\"},\"description\":{\"path\":{\"offset\":{\"ll\":{\"nodes\":{\"NodeLL\":[{\"delta\":{\"node-LL1\":{\"lon\":\"1527\",\"lat\":\"-659\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"5545\",\"lat\":\"-2394\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"9493\",\"lat\":\"-2736\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"7465\",\"lat\":\"-1304\"}}},{\"delta\":{\"node-LL4\":{\"lon\":\"34464\",\"lat\":\"-4324\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"9994\",\"lat\":\"-1119\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"20051\",\"lat\":\"-2246\"}}},{\"delta\":{\"node-LL3\":{\"lon\":\"31738\",\"lat\":\"-1775\"}}},{\"delta\":{\"node-LL2\":{\"lon\":\"5744\",\"lat\":\"-315\"}}}]}}},\"scale\":\"0\"}},\"id\":{\"id\":\"0\",\"region\":\"0\"},\"direction\":\"0000110000000000\"}},\"startYear\":\"2024\",\"notUsed2\":\"0\",\"msgId\":{\"roadSignID\":{\"viewAngle\":\"1111111111111111\",\"mutcdCode\":{\"warning\":\"\"},\"position\":{\"lat\":\"395658598\",\"long\":\"-1050401840\"}}},\"notUsed3\":\"0\",\"notUsed1\":\"0\",\"priority\":\"5\",\"content\":{\"workZone\":{\"SEQUENCE\":{\"item\":{\"itis\":\"1025\"}}}},\"url\":\"null\",\"notUsed\":\"0\",\"frameType\":{\"advisory\":\"\"},\"startTime\":\"277110\"}},\"msgCnt\":\"1\"}}}},\"dataType\":\"TravelerInformation\"}}";

    @Autowired
    DeduplicatorProperties props;

    @Test
    public void testTopology() {

        props = new DeduplicatorProperties();
        props.setKafkaTopicOdeTimJson(inputTopic);
        props.setKafkaTopicDeduplicatedOdeTimJson(outputTopic);

        TimDeduplicatorTopology TimDeduplicatorTopology = new TimDeduplicatorTopology(props, null);

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
