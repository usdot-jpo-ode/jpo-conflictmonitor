package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.spat.SpatValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.testutils.TopologyTestUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpatValidationTopologyTest {

    private final static Logger logger = LoggerFactory.getLogger(SpatValidationTopologyTest.class);

    final String inputTopicName = "topic.ProcessedSpat";
    final String broadcastRateTopicName = "topic.CmSpatBroadcastRateEvents";
    final String minimumDataTopicName = "topic.CmSpatMinimumDataEvents";
    
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
    final String source = "{ rsuId='127.0.0.1', intersectionId='11111', region='0'}";
    final int intersectionId = 11111;

    @Test
    public void testMapValidationTopology() {
        var parameters = getParameters();
        var streamsConfig = new Properties();
        streamsConfig.setProperty(
            StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, 
            TimestampExtractorForBroadcastRate.class.getName());

        var spatValidationTopology = new SpatValidationTopology();
        spatValidationTopology.setParameters(parameters);
        Topology topology = spatValidationTopology.buildTopology();

        

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

            final RsuIntersectionKey key = new RsuIntersectionKey(rsuId, intersectionId);

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


    
}
