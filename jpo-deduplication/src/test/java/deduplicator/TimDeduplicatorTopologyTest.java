package deduplicator;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import us.dot.its.jpo.deduplication.deduplicator.topologies.TimDeduplicatorTopology;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class TimDeduplicatorTopologyTest {

    String inputTopic = "topic.OdeTimJson";
    String outputTopic = "topic.DeduplicatedOdeTimJson";

    ObjectMapper objectMapper = new ObjectMapper();

    String inputTim1 = "{\"metadata\": {\"request\": {\"ode\": {\"verb\": \"POST\", \"version\": \"3\"}, \"rsus\": {\"rsus\": {\"rsuTarget\": \"10.16.28.6\", \"snmpProtocol\": \"FOURDOT1\", \"rsuRetries\": \"3\", \"rsuTimeout\": \"5000\", \"rsuIndex\": \"2\"}}, \"snmp\": {\"mode\": \"1\", \"deliverystop\": \"2024-02-09T00:00:00Z\", \"rsuid\": \"83\", \"deliverystart\": \"2024-01-31T14:00:00Z\", \"enable\": \"1\", \"channel\": \"183\", \"msgid\": \"31\", \"interval\": \"1000\", \"status\": \"4\"}}, \"recordGeneratedBy\": \"TMC\", \"schemaVersion\": \"6\", \"payloadType\": \"us.dot.its.jpo.ode.model.OdeTimPayload\", \"odePacketID\": \"845A5C6FC7C2C059FB\", \"serialId\": {\"recordId\": \"0\", \"serialNumber\": \"5517\", \"streamId\": \"928c6574-a1ed-4cec-98e0-695e593491e6\", \"bundleSize\": \"1\", \"bundleId\": \"5517\"}, \"sanitized\": \"false\", \"recordGeneratedAt\": \"2024-01-31T14:14:00Z\", \"maxDurationTime\": \"30\", \"odeTimStartDateTime\": \"2024-02-07T03:30:35.436Z\", \"odeReceivedAt\": \"2024-02-07T03:30:36.414694063Z\"}, \"payload\": {\"data\": {\"MessageFrame\": {\"messageId\": \"31\", \"value\": {\"TravelerInformation\": {\"timeStamp\": \"44054\", \"packetID\": \"845A5C6FC7C2C059FB\", \"urlB\": \"null\", \"dataFrames\": {\"TravelerDataFrame\": {\"durationTime\": \"30\", \"regions\": {\"GeographicalPath\": {\"closedPath\": {\"false\": \"\"}, \"anchor\": {\"lat\": \"388147608\", \"long\": \"-1048222621\"}, \"name\": \"I_CO-115_RSU_10.16.28.6\", \"laneWidth\": \"5000\", \"directionality\": {\"both\": \"\"}, \"description\": {\"path\": {\"offset\": {\"ll\": {\"nodes\": {\"NodeLL\": [{\"delta\": {\"node-LL1\": {\"lon\": \"4\", \"lat\": \"-1349\"}}}, {\"delta\": {\"node-LL2\": {\"lon\": \"20\", \"lat\": \"-6262\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"77\", \"lat\": \"-24093\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"86\", \"lat\": \"-19784\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"433\", \"lat\": \"-28636\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"547\", \"lat\": \"-23879\"}}}, {\"delta\": {\"node-LL4\": {\"lon\": \"601\", \"lat\": \"-68846\"}}}, {\"delta\": {\"node-LL4\": {\"lon\": \"-55\", \"lat\": \"-36419\"}}}, {\"delta\": {\"node-LL2\": {\"lon\": \"68\", \"lat\": \"-3774\"}}}]}}}, \"scale\": \"0\"}}, \"id\": {\"id\": \"0\", \"region\": \"0\"}, \"direction\": \"0000000110000000\"}}, \"startYear\": \"2024\", \"notUsed2\": \"0\", \"msgId\": {\"roadSignID\": {\"viewAngle\": \"1111111111111111\", \"mutcdCode\": {\"warning\": \"\"}, \"position\": {\"lat\": \"388147608\", \"long\": \"-1048222621\"}}}, \"notUsed3\": \"0\", \"notUsed1\": \"0\", \"priority\": \"5\", \"content\": {\"workZone\": {\"SEQUENCE\": {\"item\": {\"itis\": \"1025\"}}}}, \"url\": \"null\", \"notUsed\": \"0\", \"frameType\": {\"advisory\": \"\"}, \"startTime\": \"53490\"}}, \"msgCnt\": \"1\"}}}}, \"dataType\": \"TravelerInformation\"}, \"recordGeneratedAt\": {\"$date\": \"2024-02-07T03:30:36.415Z\"}}";
    String inputTim2 = "{\"metadata\": {\"request\": {\"ode\": {\"verb\": \"POST\", \"version\": \"3\"}, \"rsus\": {\"rsus\": {\"rsuTarget\": \"10.16.28.6\", \"snmpProtocol\": \"FOURDOT1\", \"rsuRetries\": \"3\", \"rsuTimeout\": \"5000\", \"rsuIndex\": \"2\"}}, \"snmp\": {\"mode\": \"1\", \"deliverystop\": \"2024-02-09T00:00:00Z\", \"rsuid\": \"83\", \"deliverystart\": \"2024-01-31T14:00:00Z\", \"enable\": \"1\", \"channel\": \"183\", \"msgid\": \"31\", \"interval\": \"1000\", \"status\": \"4\"}}, \"recordGeneratedBy\": \"TMC\", \"schemaVersion\": \"6\", \"payloadType\": \"us.dot.its.jpo.ode.model.OdeTimPayload\", \"odePacketID\": \"845A5C6FC7C2C059FB\", \"serialId\": {\"recordId\": \"0\", \"serialNumber\": \"5517\", \"streamId\": \"928c6574-a1ed-4cec-98e0-695e593491e6\", \"bundleSize\": \"1\", \"bundleId\": \"5517\"}, \"sanitized\": \"false\", \"recordGeneratedAt\": \"2024-01-31T14:14:00Z\", \"maxDurationTime\": \"30\", \"odeTimStartDateTime\": \"2024-02-07T03:30:35.436Z\", \"odeReceivedAt\": \"2024-02-07T03:30:36.414694063Z\"}, \"payload\": {\"data\": {\"MessageFrame\": {\"messageId\": \"31\", \"value\": {\"TravelerInformation\": {\"timeStamp\": \"44054\", \"packetID\": \"845A5C6FC7C2C059FB\", \"urlB\": \"null\", \"dataFrames\": {\"TravelerDataFrame\": {\"durationTime\": \"30\", \"regions\": {\"GeographicalPath\": {\"closedPath\": {\"false\": \"\"}, \"anchor\": {\"lat\": \"388147608\", \"long\": \"-1048222621\"}, \"name\": \"I_CO-115_RSU_10.16.28.6\", \"laneWidth\": \"5000\", \"directionality\": {\"both\": \"\"}, \"description\": {\"path\": {\"offset\": {\"ll\": {\"nodes\": {\"NodeLL\": [{\"delta\": {\"node-LL1\": {\"lon\": \"4\", \"lat\": \"-1349\"}}}, {\"delta\": {\"node-LL2\": {\"lon\": \"20\", \"lat\": \"-6262\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"77\", \"lat\": \"-24093\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"86\", \"lat\": \"-19784\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"433\", \"lat\": \"-28636\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"547\", \"lat\": \"-23879\"}}}, {\"delta\": {\"node-LL4\": {\"lon\": \"601\", \"lat\": \"-68846\"}}}, {\"delta\": {\"node-LL4\": {\"lon\": \"-55\", \"lat\": \"-36419\"}}}, {\"delta\": {\"node-LL2\": {\"lon\": \"68\", \"lat\": \"-3774\"}}}]}}}, \"scale\": \"0\"}}, \"id\": {\"id\": \"0\", \"region\": \"0\"}, \"direction\": \"0000000110000000\"}}, \"startYear\": \"2024\", \"notUsed2\": \"0\", \"msgId\": {\"roadSignID\": {\"viewAngle\": \"1111111111111111\", \"mutcdCode\": {\"warning\": \"\"}, \"position\": {\"lat\": \"388147608\", \"long\": \"-1048222621\"}}}, \"notUsed3\": \"0\", \"notUsed1\": \"0\", \"priority\": \"5\", \"content\": {\"workZone\": {\"SEQUENCE\": {\"item\": {\"itis\": \"1025\"}}}}, \"url\": \"null\", \"notUsed\": \"0\", \"frameType\": {\"advisory\": \"\"}, \"startTime\": \"53490\"}}, \"msgCnt\": \"1\"}}}}, \"dataType\": \"TravelerInformation\"}, \"recordGeneratedAt\": {\"$date\": \"2024-02-07T03:30:46.415Z\"}}";
    String inputTim3 = "{\"metadata\": {\"request\": {\"ode\": {\"verb\": \"POST\", \"version\": \"3\"}, \"rsus\": {\"rsus\": {\"rsuTarget\": \"10.16.28.6\", \"snmpProtocol\": \"FOURDOT1\", \"rsuRetries\": \"3\", \"rsuTimeout\": \"5000\", \"rsuIndex\": \"2\"}}, \"snmp\": {\"mode\": \"1\", \"deliverystop\": \"2024-02-09T00:00:00Z\", \"rsuid\": \"83\", \"deliverystart\": \"2024-01-31T14:00:00Z\", \"enable\": \"1\", \"channel\": \"183\", \"msgid\": \"31\", \"interval\": \"1000\", \"status\": \"4\"}}, \"recordGeneratedBy\": \"TMC\", \"schemaVersion\": \"6\", \"payloadType\": \"us.dot.its.jpo.ode.model.OdeTimPayload\", \"odePacketID\": \"845A5C6FC7C2C059FC\", \"serialId\": {\"recordId\": \"0\", \"serialNumber\": \"5517\", \"streamId\": \"928c6574-a1ed-4cec-98e0-695e593491e6\", \"bundleSize\": \"1\", \"bundleId\": \"5517\"}, \"sanitized\": \"false\", \"recordGeneratedAt\": \"2024-01-31T14:14:00Z\", \"maxDurationTime\": \"30\", \"odeTimStartDateTime\": \"2024-02-07T03:30:35.436Z\", \"odeReceivedAt\": \"2024-02-07T03:30:36.414694063Z\"}, \"payload\": {\"data\": {\"MessageFrame\": {\"messageId\": \"31\", \"value\": {\"TravelerInformation\": {\"timeStamp\": \"44054\", \"packetID\": \"845A5C6FC7C2C059FB\", \"urlB\": \"null\", \"dataFrames\": {\"TravelerDataFrame\": {\"durationTime\": \"30\", \"regions\": {\"GeographicalPath\": {\"closedPath\": {\"false\": \"\"}, \"anchor\": {\"lat\": \"388147608\", \"long\": \"-1048222621\"}, \"name\": \"I_CO-115_RSU_10.16.28.6\", \"laneWidth\": \"5000\", \"directionality\": {\"both\": \"\"}, \"description\": {\"path\": {\"offset\": {\"ll\": {\"nodes\": {\"NodeLL\": [{\"delta\": {\"node-LL1\": {\"lon\": \"4\", \"lat\": \"-1349\"}}}, {\"delta\": {\"node-LL2\": {\"lon\": \"20\", \"lat\": \"-6262\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"77\", \"lat\": \"-24093\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"86\", \"lat\": \"-19784\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"433\", \"lat\": \"-28636\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"547\", \"lat\": \"-23879\"}}}, {\"delta\": {\"node-LL4\": {\"lon\": \"601\", \"lat\": \"-68846\"}}}, {\"delta\": {\"node-LL4\": {\"lon\": \"-55\", \"lat\": \"-36419\"}}}, {\"delta\": {\"node-LL2\": {\"lon\": \"68\", \"lat\": \"-3774\"}}}]}}}, \"scale\": \"0\"}}, \"id\": {\"id\": \"0\", \"region\": \"0\"}, \"direction\": \"0000000110000000\"}}, \"startYear\": \"2024\", \"notUsed2\": \"0\", \"msgId\": {\"roadSignID\": {\"viewAngle\": \"1111111111111111\", \"mutcdCode\": {\"warning\": \"\"}, \"position\": {\"lat\": \"388147608\", \"long\": \"-1048222621\"}}}, \"notUsed3\": \"0\", \"notUsed1\": \"0\", \"priority\": \"5\", \"content\": {\"workZone\": {\"SEQUENCE\": {\"item\": {\"itis\": \"1025\"}}}}, \"url\": \"null\", \"notUsed\": \"0\", \"frameType\": {\"advisory\": \"\"}, \"startTime\": \"53490\"}}, \"msgCnt\": \"1\"}}}}, \"dataType\": \"TravelerInformation\"}, \"recordGeneratedAt\": {\"$date\": \"2024-02-07T03:30:56.415Z\"}}";
    String inputTim4 = "{\"metadata\": {\"request\": {\"ode\": {\"verb\": \"POST\", \"version\": \"3\"}, \"rsus\": {\"rsus\": {\"rsuTarget\": \"10.16.28.6\", \"snmpProtocol\": \"FOURDOT1\", \"rsuRetries\": \"3\", \"rsuTimeout\": \"5000\", \"rsuIndex\": \"2\"}}, \"snmp\": {\"mode\": \"1\", \"deliverystop\": \"2024-02-09T00:00:00Z\", \"rsuid\": \"83\", \"deliverystart\": \"2024-01-31T14:00:00Z\", \"enable\": \"1\", \"channel\": \"183\", \"msgid\": \"31\", \"interval\": \"1000\", \"status\": \"4\"}}, \"recordGeneratedBy\": \"TMC\", \"schemaVersion\": \"6\", \"payloadType\": \"us.dot.its.jpo.ode.model.OdeTimPayload\", \"odePacketID\": \"845A5C6FC7C2C059FB\", \"serialId\": {\"recordId\": \"0\", \"serialNumber\": \"5517\", \"streamId\": \"928c6574-a1ed-4cec-98e0-695e593491e6\", \"bundleSize\": \"1\", \"bundleId\": \"5517\"}, \"sanitized\": \"false\", \"recordGeneratedAt\": \"2024-01-31T14:14:00Z\", \"maxDurationTime\": \"30\", \"odeTimStartDateTime\": \"2024-02-07T03:30:35.436Z\", \"odeReceivedAt\": \"2024-02-07T03:30:36.414694063Z\"}, \"payload\": {\"data\": {\"MessageFrame\": {\"messageId\": \"31\", \"value\": {\"TravelerInformation\": {\"timeStamp\": \"44054\", \"packetID\": \"845A5C6FC7C2C059FB\", \"urlB\": \"null\", \"dataFrames\": {\"TravelerDataFrame\": {\"durationTime\": \"30\", \"regions\": {\"GeographicalPath\": {\"closedPath\": {\"false\": \"\"}, \"anchor\": {\"lat\": \"388147608\", \"long\": \"-1048222621\"}, \"name\": \"I_CO-115_RSU_10.16.28.6\", \"laneWidth\": \"5000\", \"directionality\": {\"both\": \"\"}, \"description\": {\"path\": {\"offset\": {\"ll\": {\"nodes\": {\"NodeLL\": [{\"delta\": {\"node-LL1\": {\"lon\": \"4\", \"lat\": \"-1349\"}}}, {\"delta\": {\"node-LL2\": {\"lon\": \"20\", \"lat\": \"-6262\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"77\", \"lat\": \"-24093\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"86\", \"lat\": \"-19784\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"433\", \"lat\": \"-28636\"}}}, {\"delta\": {\"node-LL3\": {\"lon\": \"547\", \"lat\": \"-23879\"}}}, {\"delta\": {\"node-LL4\": {\"lon\": \"601\", \"lat\": \"-68846\"}}}, {\"delta\": {\"node-LL4\": {\"lon\": \"-55\", \"lat\": \"-36419\"}}}, {\"delta\": {\"node-LL2\": {\"lon\": \"68\", \"lat\": \"-3774\"}}}]}}}, \"scale\": \"0\"}}, \"id\": {\"id\": \"0\", \"region\": \"0\"}, \"direction\": \"0000000110000000\"}}, \"startYear\": \"2024\", \"notUsed2\": \"0\", \"msgId\": {\"roadSignID\": {\"viewAngle\": \"1111111111111111\", \"mutcdCode\": {\"warning\": \"\"}, \"position\": {\"lat\": \"388147608\", \"long\": \"-1048222621\"}}}, \"notUsed3\": \"0\", \"notUsed1\": \"0\", \"priority\": \"5\", \"content\": {\"workZone\": {\"SEQUENCE\": {\"item\": {\"itis\": \"1025\"}}}}, \"url\": \"null\", \"notUsed\": \"0\", \"frameType\": {\"advisory\": \"\"}, \"startTime\": \"53490\"}}, \"msgCnt\": \"1\"}}}}, \"dataType\": \"TravelerInformation\"}, \"recordGeneratedAt\": {\"$date\": \"2024-02-07T04:31:36.415Z\"}}";

    

    @Test
    public void testTopology() {

        TimDeduplicatorTopology TimDeduplicatorTopology = new TimDeduplicatorTopology(inputTopic, outputTopic, null);

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

            List<KeyValue<String, String>> mapDeduplicationResults = outputTimData.readKeyValuesToList();

            // validate that only 3 messages make it through
            assertEquals(3, mapDeduplicationResults.size());


            assertEquals(inputTim1, mapDeduplicationResults.get(0).value);
            assertEquals(inputTim3, mapDeduplicationResults.get(1).value);
            assertEquals(inputTim4, mapDeduplicationResults.get(2).value);
        
        }catch(Exception e){
            e.printStackTrace(); 
        }
    }
}
