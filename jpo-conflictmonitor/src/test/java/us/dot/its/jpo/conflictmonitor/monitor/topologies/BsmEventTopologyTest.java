package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.ode.model.OdeBsmData;

import java.time.Instant;
import java.util.List;

public class BsmEventTopologyTest {

    final String inputTopicName = "topic.OdeBsmJson";
    final String outputTopicName = "topic.CMBsmEvents";
    final String stateStoreName = "bsm-event-state-store";

    //@Test
    public void testBsmEventTopology() {
        var parameters = getParameters();
        var streamsConfig = new Properties();
        streamsConfig.getProperty(
            StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, 
            BsmTimestampExtractor.class.getName());
        
        var bsmEventTopology = new BsmEventTopology();
        bsmEventTopology.setParameters(parameters);
        Topology topology = bsmEventTopology.build();
        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {
            
            var inputTopic = driver.createInputTopic(inputTopicName,
                Serdes.String().serializer(),
                JsonSerdes.OdeBsm().serializer());

            var outputTopic = driver.createOutputTopic(outputTopicName,
                Serdes.String().deserializer(),
                JsonSerdes.BsmEvent().deserializer()
            );

            // Start time on 10-second window boundary
            final Instant startTime = Instant.ofEpochMilli(1674356320000L);
            final int periodMillis = 100;
            final int totalTimeSeconds = 31;
            List<Instant> instants = TopologyTestUtils.getInstants(startTime, periodMillis, totalTimeSeconds);
            for (var currentInstant : instants) {
                OdeBsmData bsm = createBsm(currentInstant);
                inputTopic.pipeInput(null, bsm, currentInstant);
            }

            
        }
    }

    private BsmEventParameters getParameters() {
        var parameters = new BsmEventParameters();
        parameters.setInputTopic(inputTopicName);
        parameters.setOutputTopic(outputTopicName);
        parameters.setStateStoreName(stateStoreName);
        return parameters;
    }

    private OdeBsmData createBsm(Instant instant) {
        return new OdeBsmData();
    }
    
}
