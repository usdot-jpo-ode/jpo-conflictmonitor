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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.SpatMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.revocable_enabled_lane_alignment.RevocableEnabledLaneAlignmentEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.RevocableEnabledLaneAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;

import us.dot.its.jpo.conflictmonitor.testutils.ResourceUtils;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;


@Slf4j
@RunWith(Parameterized.class)
public class RevocableEnabledLaneAlignmentTopologyTest {

    final SpatMap spatMap;
    final boolean expectEvent;

    public RevocableEnabledLaneAlignmentTopologyTest(SpatMap spatMap, boolean expectEvent) {
        this.spatMap = spatMap;
        this.expectEvent = expectEvent;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() throws JsonProcessingException {
        var params = new ArrayList<Object[]>();
        params.add(params("RevocableLanes_ProcessedSpat_non_revocable_enabled.json", true));
        params.add(params("RevocableLanes_ProcessedSpat_non_existent_enabled.json", true));
        params.add(params("RevocableLanes_ProcessedSpat_no_event_1.json", false));
        params.add(params("RevocableLanes_ProcessedSpat_no_event_2.json", false));
        return params;
    }

    private static Object[] params(final String spatResourceName, final boolean expectEvent)
            throws JsonProcessingException {
        return new Object[] { getSpatMap(spatResourceName), expectEvent};
    }

    final Properties streamsProperties = new Properties();
    final String spatMapTopicName = "topic.SpatMap";
    final String eventTopicName = "topic.CmRevocableEnabledLaneAlignment";
    final String notificationTopicName = "topic.CmRevocableEnabledLaneAlignmentNotification";
    final String aggNotificationTopicName = "topic.CmRevocableEnabledLaneAlignmentNotificationAggregation";
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

            var notificationTopic = driver.createOutputTopic(notificationTopicName,
                    new JsonDeserializer<>(RsuIntersectionKey.class),
                    new JsonDeserializer<>(RevocableEnabledLaneAlignmentNotification.class));

            final var rsuKey = new RsuIntersectionKey(rsuId, intersectionId);

            log.debug("SpatMap: {}", spatMap);

            spatMapTopic.pipeInput(rsuKey, spatMap);

            List<KeyValue<RsuIntersectionKey, RevocableEnabledLaneAlignmentEvent>> events =
                eventTopic.readKeyValuesToList();

            for (var event : events) {
                log.debug("Event: {}", event);
            }

            if (expectEvent) {
                assertThat("expected 1 event",events, hasSize(1));
            } else {
                assertThat("expected no events", events, hasSize(0));
            }

            List<KeyValue<RsuIntersectionKey, RevocableEnabledLaneAlignmentNotification>> notifications =
                    notificationTopic.readKeyValuesToList();

//            if (expectEvent) {
//                assertThat("expected 1 notification",notifications, hasSize(1));
//            } else {
//                assertThat("expected no notifications", notifications, hasSize(0));
//            }

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
        parameters.setNotificationTopicName(notificationTopicName);
        parameters.setAggNotificationTopicName(aggNotificationTopicName);
        return parameters;
    }


    private static SpatMap getSpatMap(final String spatResourceName) throws JsonProcessingException {
        return new SpatMap(getProcessedSpat(spatResourceName), getProcessedMap());
    }

    private static final String RESOURCE_PATH = "/us/dot/its/jpo/conflictmonitor/monitor/topologies/";

    private static ProcessedSpat getProcessedSpat(final String spatResourceName) throws JsonProcessingException {
        String spatStr = ResourceUtils.loadResource(RESOURCE_PATH + spatResourceName);
        ObjectMapper mapper = DateJsonMapper.getInstance();
        return mapper.readValue(spatStr, ProcessedSpat.class);
    }

    @SuppressWarnings({"unchecked"})
    private static ProcessedMap<LineString> getProcessedMap() throws JsonProcessingException {
        String mapStr = ResourceUtils.loadResource(RESOURCE_PATH + "RevocableLanes_ProcessedMap.json");
        ObjectMapper mapper = DateJsonMapper.getInstance();
        return (ProcessedMap<LineString>)mapper.readValue(mapStr, ProcessedMap.class);
    }
}
