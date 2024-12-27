package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import java.util.Properties;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.processor.PunctuationType;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.bsm_event.BsmEventParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionIdKey;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.testutils.TopologyTestUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuLogKey;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils.bsmAtInstant;
import static us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils.processedBsmAtInstant;

public class BsmEventTopologyTest {

    private static final Logger logger = LoggerFactory.getLogger(BsmEventTopologyTest.class);

    final String inputTopicName = "topic.OdeBsmJson";
    final String outputTopicName = "topic.CMBsmEvents";
    final String bsmIntersectionOutputTopic = "topic.CmBsmIntersection";
    final String stateStoreName = "bsm-event-state-store";
    final String mapSpatialStoreName = "map-spatial-store";
    final String mapBoundingBoxTopic = "topic.MapBoundingBox";

    final String rsuId = "127.0.0.1";


    @Test
    public void testBsmEventTopology() {
        var parameters = getParameters();
        var streamsConfig = new Properties();
        streamsConfig.setProperty(
            StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, 
            BsmTimestampExtractor.class.getName());
        
        var bsmEventTopology = new BsmEventTopology();
        bsmEventTopology.setParameters(parameters);
        bsmEventTopology.setPunctuationType(PunctuationType.STREAM_TIME);
        var streamsProperties = new Properties();
        bsmEventTopology.setStreamsProperties(streamsProperties);
        var mapIndex = new MapIndex();
        bsmEventTopology.setMapIndex(mapIndex);
        bsmEventTopology.validate();

        Topology topology = bsmEventTopology.buildTopology();
        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {

            var inputTopic = driver.createInputTopic(inputTopicName,
                    new JsonSerializer<RsuLogKey>(),
                    new JsonSerializer<ProcessedBsm<Point>>());

            var outputTopic = driver.createOutputTopic(outputTopicName,
                new JsonDeserializer<>(BsmIntersectionIdKey.class),
                new JsonDeserializer<>(BsmEvent.class));

            final Instant startTime = Instant.ofEpochMilli(1674356320000L);
            final int periodMillis = 100;
            final int totalTimeSeconds = 1;
            List<Instant> instants = TopologyTestUtils.getInstantsExclusive(startTime, periodMillis, totalTimeSeconds);
            final RsuLogKey id1 = new RsuLogKey();
            id1.setRsuId(rsuId);
            id1.setBsmId("BSMID1");
            for (var currentInstant : instants) {
                logger.info("Send BSM at {}", currentInstant);
                ProcessedBsm<Point> bsm = processedBsmAtInstant(currentInstant, id1.getBsmId());
                inputTopic.pipeInput(id1, bsm, currentInstant);
            }

            // Simulate a long enough period of no BSMs followed by a different BSM ID to advance stream time
            final Instant newBsm = startTime.plusSeconds(15);
            final RsuLogKey id2 = new RsuLogKey();
            id2.setRsuId(rsuId);
            id2.setBsmId("BSMID2");
            ProcessedBsm<Point> gapBsm = processedBsmAtInstant(newBsm, id2.getBsmId());
            inputTopic.pipeInput(id2, gapBsm, newBsm);

            // Include an invalid BSM for validation coverage
            ProcessedBsm<Point> invalidBsm = processedBsmAtInstant(startTime, id2.getBsmId());
            var validationMessage = new ProcessedValidationMessage();
            validationMessage.setMessage("Invalid BSM");
            invalidBsm.setValidationMessages(List.of(validationMessage));
            inputTopic.pipeInput(id2, invalidBsm, startTime);

            // Include a BSM with an earlier timestamp than the previous BSM for validation coverage
            final Instant oldTime = startTime.minusSeconds(100);
            ProcessedBsm<Point> oldBsm = processedBsmAtInstant(oldTime, id2.getBsmId());
            inputTopic.pipeInput(id2, oldBsm, oldTime);

            var output = outputTopic.readKeyValuesToList();
            assertThat(output, hasSize(1));
            var outputItem = output.iterator().next();
            logger.info("BSM Event: {}", outputItem);
            BsmIntersectionIdKey key = outputItem.key;
            assertThat(key.getBsmId(), endsWith(id1.getBsmId()));
            var value = outputItem.value;
            assertThat(value.getEndingBsmTimestamp(), notNullValue());
            assertThat(value.getStartingBsmTimestamp(), notNullValue());
            assertThat(value.getEndingBsmTimestamp() - value.getStartingBsmTimestamp(), equalTo(1000L));
        }
    }

    private BsmEventParameters getParameters() {
        var parameters = new BsmEventParameters();
        parameters.setInputTopic(inputTopicName);
        parameters.setOutputTopic(outputTopicName);
        parameters.setBsmIntersectionOutputTopic(bsmIntersectionOutputTopic);
        parameters.setStateStoreName(stateStoreName);
        return parameters;
    }


    
}
