package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.spat.SpatTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.SpatTimestampDeltaEvent;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@Slf4j
public class SpatValidationTopologyTest {

    final String inputTopicName = "topic.ProcessedSpat";
    final String broadcastRateTopicName = "topic.CmSpatBroadcastRateEvents";
    final String minimumDataTopicName = "topic.CmSpatMinimumDataEvents";
    final int maxDeltaMilliseconds = 50;
    final String timestampOutputTopicName = "topic.CmTimestampDeltaEvent";
    final String keyStoreName = "spatTimestampDeltaKeyStore";
    final String eventStoreName = "spatTimestampDeltaEventStore";
    final int retentionTimeMinutes = 60;
    final String notificationTopicName = "topic.CmTimestampDeltaNotification";
    
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

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig);
             Serde<RsuIntersectionKey> rsuIntersectionKeySerde
                     = us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
             Serde<ProcessedSpat> processedSpatSerde
                     = us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat();
             Serde<SpatBroadcastRateEvent> spatBroadcastRateEventSerde = JsonSerdes.SpatBroadcastRateEvent();
             Serde<SpatMinimumDataEvent> spatMinimumDataEventSerde = JsonSerdes.SpatMinimumDataEvent()) {

            var inputTopic = driver.createInputTopic(inputTopicName,
                    rsuIntersectionKeySerde.serializer(), processedSpatSerde.serializer());

            var broadcastRateTopic = driver.createOutputTopic(broadcastRateTopicName,
                    rsuIntersectionKeySerde.deserializer(), spatBroadcastRateEventSerde.deserializer());

            var minimumDataTopic = driver.createOutputTopic(minimumDataTopicName,
                    rsuIntersectionKeySerde.deserializer(), spatMinimumDataEventSerde.deserializer());

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
                var msg = result.getMissingDataElements().getFirst();
                assertThat("min data validation message match", msg, startsWith(validationMsg));
            }

            var broadcastRateList = broadcastRateTopic.readKeyValuesToList();
            assertThat("Should be 1 broadcast rate event", broadcastRateList, hasSize(1));
            var broadcastRate = broadcastRateList.getFirst();
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

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig);
             Serde<RsuIntersectionKey> rsuIntersectionKeySerde = us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
             Serde<ProcessedSpat> processedSpatSerde = us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat();
             Serde<SpatTimestampDeltaEvent> spatTimestampDeltaEventSerde = JsonSerdes.SpatTimestampDeltaEvent()) {

            var inputTopic = driver.createInputTopic(inputTopicName,
                    rsuIntersectionKeySerde.serializer(), processedSpatSerde.serializer());

            var timestampDeltaEventTopic = driver.createOutputTopic(timestampOutputTopicName,
                    rsuIntersectionKeySerde.deserializer(), spatTimestampDeltaEventSerde.deserializer());

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

            final Set<Long> expectDeltas = ImmutableSet.of(500L, -20L, -600L);
            final int numberOfEventsExpected = expectDeltas.size();

            assertThat(String.format("%s of 4 inputs should have produced timestamp delta events", numberOfEventsExpected),
                    timestampEventList, hasSize(numberOfEventsExpected));


            Set<Long> actualDeltas = timestampEventList.stream().map(entry -> entry.value.getDelta().getDeltaMillis()).collect(toSet());
            assertThat(String.format("Expect deltas: %s.  Actual deltas: %s", expectDeltas, actualDeltas), Sets.symmetricDifference(expectDeltas, actualDeltas), hasSize(0));
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
        parameters.setKeyStoreName(keyStoreName);
        parameters.setEventStoreName(eventStoreName);
        parameters.setRetentionTimeMinutes(retentionTimeMinutes);
        parameters.setNotificationTopicName(notificationTopicName);
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

    private ProcessedSpat createSpatWithTimestampOffset(Instant timestamp, Instant odeReceivedAt) {
        var spat = new ProcessedSpat();
        spat.setOdeReceivedAt(odeReceivedAt.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME));
        spat.setUtcTimeStamp(timestamp.atZone(ZoneOffset.UTC));
        spat.setCti4501Conformant(true);
        return spat;
    }


    
}
