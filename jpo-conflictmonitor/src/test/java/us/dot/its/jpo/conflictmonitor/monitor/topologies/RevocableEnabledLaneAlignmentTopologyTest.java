package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import us.dot.its.jpo.conflictmonitor.testutils.ResourceUtils;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;


@Slf4j
public class RevocableEnabledLaneAlignmentTopologyTest {

    final Properties streamsProperties = new Properties();
    final String spatMapTopicName = "topic.SpatMap";
    final String eventTopicName = "topic.CmRevocableEnabledLaneAlignment";
    final boolean debug = true;
    final boolean aggregateEvents = false;
    final String rsuId = "172.18.0.1";
    final int intersectionId = 3416;

    @Test
    public void testRevocableEnabledLaneAlignmentTopology() throws JsonProcessingException {

        Topology topology = createTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsProperties)) {

            var spatMapTopic = driver.createInputTopic(spatMapTopicName,
                    new JsonSerializer<RsuIntersectionKey>(),
                    new JsonSerializer<SpatMap>());

            var eventTopic = driver.createOutputTopic(eventTopicName,
                    new JsonDeserializer<>(RsuIntersectionKey.class),
                    new JsonDeserializer<>(RevocableEnabledLaneAlignmentEvent.class));

            final var rsuKey = new RsuIntersectionKey(rsuId, intersectionId);
            final var spatMap = getSpatMap();
            spatMapTopic.pipeInput(rsuKey, spatMap);

            List<KeyValue<RsuIntersectionKey, RevocableEnabledLaneAlignmentEvent>> events =
                eventTopic.readKeyValuesToList();

            assertThat(events, hasSize(1));

        }
    }

    private Topology createTopology() {
        var parameters = getRevocableEnabledLaneAlignmentParameters();
        var revocableEnabledLaneAlignmentTopology = new RevocableEnabledLaneAlignmentTopology();
        revocableEnabledLaneAlignmentTopology.setParameters(parameters);
        var streamsBuilder = new StreamsBuilder();

        // Simulate the joined spat-map stream for input to the algorithm under test
        KStream<RsuIntersectionKey, SpatMap> spatMapStream =
                streamsBuilder.stream(spatMapTopicName,
                        Consumed.with(
                                us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                                JsonSerdes.SpatMap()));

        // Build the revocable/enabled topology
        revocableEnabledLaneAlignmentTopology.buildTopology(streamsBuilder, spatMapStream);
        return streamsBuilder.build();
    }

    private RevocableEnabledLaneAlignmentParameters getRevocableEnabledLaneAlignmentParameters() {
        var parameters = new RevocableEnabledLaneAlignmentParameters();
        parameters.setDebug(debug);
        parameters.setAggregateEvents(aggregateEvents);
        parameters.setOutputTopicName(eventTopicName);
        return parameters;
    }

    private SpatMap getSpatMap() throws JsonProcessingException {
        return new SpatMap(getProcessedSpat(), getProcessedMap());
    }

    private final String RESOURCE_PATH = "/us/dot/its/jpo/conflictmonitor/monitor/topologies/";

    private ProcessedSpat getProcessedSpat() throws JsonProcessingException {
        String spatStr = ResourceUtils.loadResource(RESOURCE_PATH + "RevocableLanes_ProcessedSpat_non_revocable_enabled.json");
        ObjectMapper mapper = DateJsonMapper.getInstance();
        var processedSpat = mapper.readValue(spatStr, ProcessedSpat.class);
        return processedSpat;
    }


    private ProcessedMap<LineString> getProcessedMap() throws JsonProcessingException {
        String mapStr = ResourceUtils.loadResource(RESOURCE_PATH + "RevocableLanes_ProcessedMap.json");
        ObjectMapper mapper = DateJsonMapper.getInstance();
        var processedMap = mapper.readValue(mapStr, ProcessedMap.class);
        return processedMap;
    }
}
