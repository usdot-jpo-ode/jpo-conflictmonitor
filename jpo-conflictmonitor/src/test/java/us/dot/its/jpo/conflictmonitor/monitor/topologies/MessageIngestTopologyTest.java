package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestParameters;

import java.util.Properties;

public class MessageIngestTopologyTest {

    final String bsmTopic = "topic.OdeBsmJson";
    final String spatTopic = "topic.ProcessedSpat";
    final String mapTopic = "topic.ProcessedMap";
    final String bsmStoreName = "BsmWindowStore";
    final String spatStoreName = "SpatWindowStore";
    final String mapStoreName = "ProcessedMapWindowStore";

//    @Test
    public void testMessageIngestTopology() {
        var parameters = getParamters();
        var streamsConfig = new Properties();
        var messageIngestTopology = new MessageIngestTopology();
        messageIngestTopology.setParameters(parameters);
        Topology topology = messageIngestTopology.buildTopology();
        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {

        }
    }

    MessageIngestParameters getParamters() {
        var params = new MessageIngestParameters();
        params.setBsmTopic(bsmTopic);
        params.setSpatTopic(spatTopic);
        params.setMapTopic(mapTopic);
        params.setBsmStoreName(bsmStoreName);
        params.setSpatStoreName(spatStoreName);
        params.setMapStoreName(mapStoreName);
        return params;
    }
}
