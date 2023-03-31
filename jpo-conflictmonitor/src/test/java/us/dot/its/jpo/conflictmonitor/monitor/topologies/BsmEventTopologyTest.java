package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.processor.PunctuationType;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.testutils.TopologyTestUtils;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.OdeGeoRegion;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils.bsmAtInstant;

public class BsmEventTopologyTest {

    private static final Logger logger = LoggerFactory.getLogger(BsmEventTopologyTest.class);

    final String inputTopicName = "topic.OdeBsmJson";
    final String outputTopicName = "topic.CMBsmEvents";
    final String stateStoreName = "bsm-event-state-store";

    @Test
    public void testBsmEventTopology() {
        var parameters = getParameters();
        var streamsConfig = new Properties();
        streamsConfig.getProperty(
            StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, 
            BsmTimestampExtractor.class.getName());
        
        var bsmEventTopology = new BsmEventTopology();
        bsmEventTopology.setParameters(parameters);
        bsmEventTopology.setPunctuationType(PunctuationType.STREAM_TIME);
        Topology topology = bsmEventTopology.build();
        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {
            
            var inputTopic = driver.createInputTopic(inputTopicName,
                Serdes.String().serializer(),
                JsonSerdes.OdeBsm().serializer());

            var outputTopic = driver.createOutputTopic(outputTopicName,
                Serdes.String().deserializer(),
                JsonSerdes.BsmEvent().deserializer()
            );


            final Instant startTime = Instant.ofEpochMilli(1674356320000L);
            final int periodMillis = 100;
            final int totalTimeSeconds = 30;
            List<Instant> instants = TopologyTestUtils.getInstants(startTime, periodMillis, totalTimeSeconds);
            for (var currentInstant : instants) {
                OdeBsmData bsm = bsmAtInstant(currentInstant);
                inputTopic.pipeInput(null, bsm, currentInstant);
            }

            // Simulate 15 seconds of no BSMs
            final Instant noBsmTime = startTime.plusSeconds(45);
            OdeBsmData gapBsm = bsmAtInstant(noBsmTime);
            inputTopic.pipeInput(null, gapBsm, noBsmTime);

//            // Include an invalid BSM for validation coverage
//            OdeBsmData invalidBsm = bsmAtInstant(startTime);
//            invalidBsm.setPayload(null);
//            inputTopic.pipeInput(null, invalidBsm, startTime);
//
//            // Include a BSM with an earlier timestamp than the previous BSM for validation coverage
//            final Instant oldTime = startTime.minusSeconds(100);
//            OdeBsmData oldBsm = bsmAtInstant(oldTime);
//            inputTopic.pipeInput(null, oldBsm, oldTime);

            var output = outputTopic.readKeyValuesToList();
            assertThat(output, hasSize(greaterThan(0)));
            for (var outputItem : output) {
                logger.info("Output: {}", outputItem);
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


    
}
