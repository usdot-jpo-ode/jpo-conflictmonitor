package us.dot.its.jpo.conflictmonitor.monitor.topologies.validation;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;


import us.dot.its.jpo.conflictmonitor.monitor.algorithms.timestamp_delta.map.MapTimestampDeltaParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.validation.map.MapValidationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.timestamp_delta.MapTimestampDeltaTopology;
import us.dot.its.jpo.conflictmonitor.testutils.TopologyTestUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapSharedProperties;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;

import org.junit.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class MapValidationTopologyTest {


    final String inputTopicName = "topic.ProcessedMap";
    final String broadcastRateTopicName = "topic.CmMapBroadcastRateEvents";
    final String minimumDataTopicName = "topic.CmMapMinimumDataEvents";
    final int maxDeltaMilliseconds = 50;
    final String timestampOutputTopicName = "topic.CmTimestampDeltaEvent";
    
    // Use a tumbling window for test (rolling period = output interval)
    // just to make it easier to design the test.
    final int rollingPeriod = 10;
    final int outputInterval = 10;  
    final int gracePeriod = 100;

    // Start time on 10-second window boundary
    final Instant startTime = Instant.ofEpochMilli(1674356320000L);

    final int lowerBound = 9;
    final int upperBound = 11;
    final boolean debug = true;

    final String validationMsg = "Validation Message";


    final String rsuId = "127.0.0.1";
    final String source = "{ rsuId='127.0.0.1', intersectionId='11111', region='10'}";
    final int intersectionId = 11111;
    final int region = 10;
    

    
    @Test
    public void testMapValidationTopology() {
        var parameters = getParameters();
        var streamsConfig = new Properties();
        streamsConfig.setProperty(
            StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, 
            TimestampExtractorForBroadcastRate.class.getName());

        var mapValidationTopology = new MapValidationTopology();
        mapValidationTopology.setParameters(parameters);
        var timestampTopology = new MapTimestampDeltaTopology();
        var timestampParameters = getTimestampParameters();
        timestampTopology.setParameters(timestampParameters);
        mapValidationTopology.setTimestampDeltaAlgorithm(timestampTopology);
        Topology topology = mapValidationTopology.buildTopology();

        

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {

            

            var inputTopic = driver.createInputTopic(inputTopicName,
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().serializer(), 
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedMapGeoJson().serializer()
            );

            var broadcastRateTopic = driver.createOutputTopic(broadcastRateTopicName,
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().deserializer(), 
                JsonSerdes.MapBroadcastRateEvent().deserializer()
            );

            var minimumDataTopic = driver.createOutputTopic(minimumDataTopicName,
                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().deserializer(), 
                JsonSerdes.MapMinimumDataEvent().deserializer()
            );

            final RsuIntersectionKey key = new RsuIntersectionKey(rsuId, intersectionId, region);

            // Send maps at .5 Hz (slow)
            final int slowPeriodMillis = 2000;
            final int totalTimeSeconds = 13;
            List<Instant> instants = TopologyTestUtils.getInstants(startTime, slowPeriodMillis, totalTimeSeconds);
            for (var currentInstant : instants) {
                var map = createMap(currentInstant);
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
            assertThat("broadcast rate number of messages", bcValue.getNumberOfMessages(), equalTo(5));
            assertThat("broadcast rate time period null", bcValue.getTimePeriod(), notNullValue());
            assertThat("broadcast rate time period", bcValue.getTimePeriod().periodMillis(), equalTo(10000L));
           
        }
        
    }

    private MapTimestampDeltaParameters getTimestampParameters() {
        var parameters = new MapTimestampDeltaParameters();
        parameters.setDebug(debug);
        parameters.setMaxDeltaMilliseconds(maxDeltaMilliseconds);
        parameters.setOutputTopicName(timestampOutputTopicName);
        return parameters;
    }

    private MapValidationParameters getParameters() {
        var parameters = new MapValidationParameters();
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



    private ProcessedMap<LineString> createMap(Instant timestamp) {
        var map = new ProcessedMap<LineString>();
        var props = new MapSharedProperties();
        map.setProperties(props);
        props.setOdeReceivedAt(timestamp.atZone(ZoneOffset.UTC));
        props.setCti4501Conformant(false);
        var valMsgList = new ArrayList<ProcessedValidationMessage>();
        var msg = new ProcessedValidationMessage();
        msg.setMessage(validationMsg);
        valMsgList.add(msg);
        props.setValidationMessages(valMsgList);
        return map;
    }

    

    
}
