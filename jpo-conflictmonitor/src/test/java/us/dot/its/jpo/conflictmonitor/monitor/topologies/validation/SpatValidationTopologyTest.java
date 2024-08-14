package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta.SpatTimestampDeltaTopology;
import us.dot.its.jpo.conflictmonitor.testutils.TopologyTestUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpatValidationTopologyTest {

    private final static Logger logger = LoggerFactory.getLogger(SpatValidationTopologyTest.class);

    final String inputTopicName = "topic.ProcessedSpat";
    final String broadcastRateTopicName = "topic.CmSpatBroadcastRateEvents";
    final String minimumDataTopicName = "topic.CmSpatMinimumDataEvents";
    final int maxDeltaMilliseconds = 50;
    final String timestampOutputTopicName = "topic.CmTimestampDeltaEvent";
    
    // Use a tumbling window for test (rolling period = output interval)
    // just to make it easier to design the test.
    final int rollingPeriod = 10;
    final int outputInterval = 10;  
    final int gracePeriod = 100;

    // Start time on 10-second window boundary
    final Instant startTime = Instant.ofEpochMilli(1674356320000L);

    final int lowerBound = 90;
    final int upperBound = 110;
    final boolean debug = true;

    final String validationMsg = "Validation Message";


    final String rsuId = "127.0.0.1";
    final String source = "{ rsuId='127.0.0.1', intersectionId='11111', region='10'}";
    final int intersectionId = 11111;
    final int region = 10;

    @Test
    public void testMapValidationTopology() {

        var streamsConfig = createStreamsConfig();
        Topology topology = createTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {

            var inputTopic = driver.createInputTopic(inputTopicName,
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().serializer(), 
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat().serializer()
            );

            var broadcastRateTopic = driver.createOutputTopic(broadcastRateTopicName,
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().deserializer(), 
                JsonSerdes.SpatBroadcastRateEvent().deserializer()
            );

            var minimumDataTopic = driver.createOutputTopic(minimumDataTopicName,
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().deserializer(), 
                JsonSerdes.SpatMinimumDataEvent().deserializer()
            );

            final RsuIntersectionKey key = new RsuIntersectionKey(rsuId, intersectionId, region);

            // Send maps at 5 Hz (slow)
            final int slowPeriodMillis = 200;
            final int totalTimeSeconds = 11;
            List<Instant> instants = TopologyTestUtils.getInstants(startTime, slowPeriodMillis, totalTimeSeconds);
            for (var currentInstant : instants) {
                var map = createSpat(currentInstant);
                inputTopic.pipeInput(key, map, currentInstant);
            }

            var minDataList = minimumDataTopic.readKeyValuesToList();
            assertThat("Should be > 1 min data events", minDataList, hasSize(greaterThan(1)));
            for (var entry : minDataList) {
                var resultKey = entry.key;
                assertThat("min data key rsuId", resultKey.getRsuId(), equalTo(rsuId));
                assertThat("min data key intersectionId", resultKey.getIntersectionId(), equalTo(intersectionId));
                var result = entry.value;
                assertThat("min data event rsuId", result.getSource(), equalTo(source));
                assertThat("min data event intersectionId", result.getIntersectionID(), equalTo(intersectionId));
                assertThat("min data missingDataElements size", result.getMissingDataElements(), hasSize(1));
                var msg = result.getMissingDataElements().get(0);
                assertThat("min data validation message match", msg, startsWith(validationMsg));
            }

            var broadcastRateList = broadcastRateTopic.readKeyValuesToList();
            assertThat("Should be 1 broadcast rate event", broadcastRateList, hasSize(1));
            var broadcastRate = broadcastRateList.get(0);
            var bcKey =  broadcastRate.key;
            assertThat("broadcast rate key rsuId", bcKey.getRsuId(), equalTo(rsuId));
            assertThat("broadcast rate key intersectionId", bcKey.getIntersectionId(), equalTo(intersectionId));
            var bcValue = broadcastRate.value;
            assertThat("broadcast rate device id", bcValue.getSource(), equalTo(source));
            assertThat("broadcast rate intersection id", bcValue.getIntersectionID(), equalTo(intersectionId));
            assertThat("broadcast rate topic name", bcValue.getTopicName(), equalTo(inputTopicName));
            assertThat("broadcast rate number of messages", bcValue.getNumberOfMessages(), equalTo(50));
            assertThat("broadcast rate time period null", bcValue.getTimePeriod(), notNullValue());
            assertThat("broadcast rate time period", bcValue.getTimePeriod().periodMillis(), equalTo(10000L));
           
        }
        
    }

    @Test
    public void testSpatTimestampDeltaSubtopology() {
        Properties streamsConfig = createStreamsConfig();
        Topology topology = createTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {

            var inputTopic = driver.createInputTopic(inputTopicName,
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().serializer(),
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat().serializer());

            var timestampDeltaEventTopic = driver.createOutputTopic(timestampOutputTopicName,
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().deserializer(),
                    JsonSerdes.SpatTimestampDeltaEvent().deserializer());

            final RsuIntersectionKey key = new RsuIntersectionKey(rsuId, intersectionId, region);

            final Instant start1 = startTime;
            final Instant start1_plus10 = start1.plusMillis(10L);
            final Instant start2 = start1.plusSeconds(2L);
            final Instant start2_plus500 = start2.plusMillis(500L);
            final Instant start3 = start2.plusSeconds(2L);
            final Instant start3_minus20 = start3.minusMillis(20L);
            final Instant start4 = start3.plusSeconds(2L);
            final Instant start4_minus600 = start4.minusMillis(600L);

            inputTopic.pipeInput(key, createSpatWithTimestampOffset(start1, start1_plus10), start1);
            inputTopic.pipeInput(key, createSpatWithTimestampOffset(start2, start2_plus500), start2);
            inputTopic.pipeInput(key, createSpatWithTimestampOffset(start3, start3_minus20), start3);
            inputTopic.pipeInput(key, createSpatWithTimestampOffset(start4, start4_minus600), start4);

            var timestampEventList = timestampDeltaEventTopic.readKeyValuesToList();
            assertThat("2 of 4 inputs should have produced timestamp delta events", timestampEventList, hasSize(2));

            Set<Long> expectDeltas = Set.of(500L, 600L);
            Set<Long> actualDeltas = timestampEventList.stream().map(entry -> Math.abs(entry.value.getDelta().getDeltaMillis())).collect(toSet());
            assertThat(String.format("Only the 500ms and 600ms offsets should have produced events.  Actual deltas: %s", actualDeltas), Sets.symmetricDifference(expectDeltas, actualDeltas), hasSize(0));
        }
    }

    private Topology createTopology() {
        var parameters = getParameters();
        var spatValidationTopology = new SpatValidationTopology();
        spatValidationTopology.setParameters(parameters);
        var timestampTopology = new SpatTimestampDeltaTopology();
        var timestampParameters = getTimestampParameters();
        timestampTopology.setParameters(timestampParameters);
        spatValidationTopology.setTimestampDeltaAlgorithm(timestampTopology);
        return spatValidationTopology.buildTopology();
    }

    private Properties createStreamsConfig() {
        var streamsConfig = new Properties();
        streamsConfig.setProperty(
                StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
                TimestampExtractorForBroadcastRate.class.getName());
        return streamsConfig;
    }

    private SpatTimestampDeltaParameters getTimestampParameters() {
        var parameters = new SpatTimestampDeltaParameters();
        parameters.setDebug(debug);
        parameters.setMaxDeltaMilliseconds(maxDeltaMilliseconds);
        parameters.setOutputTopicName(timestampOutputTopicName);
        return parameters;
    }

    private SpatValidationParameters getParameters() {
        var parameters = new SpatValidationParameters();
        parameters.setInputTopicName(inputTopicName);
        parameters.setBroadcastRateTopicName(broadcastRateTopicName);
        parameters.setMinimumDataTopicName(minimumDataTopicName);
        parameters.setRollingPeriodSeconds(rollingPeriod);
        parameters.setOutputIntervalSeconds(outputInterval);
        parameters.setGracePeriodMilliseconds(gracePeriod);
        parameters.setLowerBound(lowerBound);
        parameters.setUpperBound(upperBound);
        parameters.setDebug(debug);
        return parameters;
    }



    private ProcessedSpat createSpat(Instant timestamp) {
        var spat = new ProcessedSpat();
        spat.setOdeReceivedAt(timestamp.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
        spat.setCti4501Conformant(false);
        var valMsgList = new ArrayList<ProcessedValidationMessage>();
        var msg = new ProcessedValidationMessage();
        msg.setMessage(validationMsg);
        valMsgList.add(msg);
        spat.setValidationMessages(valMsgList);
        return spat;
    }

    private ProcessedSpat createSpatWithTimestampOffset(Instant odeReceivedAt, Instant timestamp) {
        var spat = new ProcessedSpat();
        spat.setOdeReceivedAt(odeReceivedAt.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
        spat.setUtcTimeStamp(timestamp.atZone(ZoneOffset.UTC));
        spat.setCti4501Conformant(true);
        return spat;
    }


    
}
