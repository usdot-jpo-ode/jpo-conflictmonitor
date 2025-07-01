package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.map.MapIndex;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuLogKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.serialization.serializers.JsonSerializer;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;


public class MessageIngestTopologyTest {

    private static final Logger logger = LoggerFactory.getLogger(MessageIngestTopologyTest.class);

    final String bsmTopicName = "topic.OdeBsmJson";
    final String spatTopicName = "topic.ProcessedSpat";
    final String mapTopicName = "topic.ProcessedMap";
    final String mapBoundingBoxTopicName = "topic.CmMapBoundingBox";
    final String bsmStoreName = "BsmWindowStore";
    final String spatStoreName = "SpatWindowStore";
    final String mapStoreName = "ProcessedMapWindowStore";
    final String rsuId = "10.11.81.12";
    final int intersectionId = 12109;
    final String mapSpatialIndexStoreName = "MapSpatialIndexStore";



    @Test
    public void testMessageIngestTopology() throws JsonProcessingException {

        // Test this plugin separately, mock it here
        var spatTransitionTopology = mock(EventStateProgressionTopology.class);
        doNothing().when(spatTransitionTopology).buildTopology(any(), any());

        var parameters = getParamters();
        var streamsConfig = new Properties();
        var mapIndex = new MapIndex();
        var messageIngestTopology = new MessageIngestTopology();
        messageIngestTopology.setParameters(parameters);
        messageIngestTopology.setMapIndex(mapIndex);
        messageIngestTopology.setEventStateProgressionAlgorithm(spatTransitionTopology);
        StreamsBuilder builder = new StreamsBuilder();
        messageIngestTopology.buildTopology(builder);
        Topology topology = builder.build();

        // Verify plugin would have been initialized
        verify(spatTransitionTopology, times(1)).buildTopology(any(), any());

        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsConfig)) {

            var bsmTopic = driver.createInputTopic(bsmTopicName,
                    new JsonSerializer<RsuLogKey>(),
                    new JsonSerializer<ProcessedBsm<Point>>());

            var spatTopic = driver.createInputTopic(spatTopicName,
                    new JsonSerializer<RsuIntersectionKey>(),
                    new JsonSerializer<ProcessedSpat>());

            var mapTopic = driver.createInputTopic(mapTopicName,
                    new JsonSerializer<RsuIntersectionKey>(),
                    new JsonSerializer<ProcessedMap<LineString>>());

            var mapBoundingBoxTopic = driver.createOutputTopic(mapBoundingBoxTopicName,
                    new JsonDeserializer<>(RsuIntersectionKey.class),
                    new StringDeserializer());

            final var rsuKey = new RsuIntersectionKey(rsuId, intersectionId);
            final var processedMap = getProcessedMap();
            mapTopic.pipeInput(rsuKey, processedMap);
            spatTopic.pipeInput(rsuKey, getProcessedSpat());
            final var bsmKey = new RsuLogKey();
            bsmKey.setBsmId("48C45782");
            bsmTopic.pipeInput(bsmKey, getBsm());

            var mapStore = driver.<RsuIntersectionKey, ProcessedMap>getKeyValueStore(mapStoreName);

            int countStoredMaps = 0;
            try (var storedMapIterator = mapStore.all()) {
                while (storedMapIterator.hasNext()) {
                    ++countStoredMaps;
                    var storedMapEntry = storedMapIterator.next();
                    var key = storedMapEntry.key;
                    assertThat(key, equalTo(rsuKey));
                    var value = storedMapEntry.value;
                    assertThat(value, notNullValue());
                }
            }
            assertThat("Number of maps", countStoredMaps, equalTo(1));

            var boundingBoxResults = mapBoundingBoxTopic.readKeyValuesToList();
            assertThat(boundingBoxResults, hasSize(equalTo(1)));
            var boundingBoxResult = boundingBoxResults.get(0);
            logger.info("Bounding box key: {}, value: {}", boundingBoxResult.key, boundingBoxResult.value);
         }
    }

    MessageIngestParameters getParamters() {
        var params = new MessageIngestParameters();
        params.setBsmTopic(bsmTopicName);
        params.setSpatTopic(spatTopicName);
        params.setMapTopic(mapTopicName);
        params.setBsmStoreName(bsmStoreName);
        params.setSpatStoreName(spatStoreName);
        params.setMapStoreName(mapStoreName);
        params.setMapBoundingBoxTopic(mapBoundingBoxTopicName);
        params.setMapSpatialIndexStoreName(mapSpatialIndexStoreName);
        return params;
    }

    ProcessedMap<LineString> getProcessedMap() throws JsonProcessingException {
        var mapper = DateJsonMapper.getInstance();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(ProcessedMap.class, LineString.class);
        return mapper.readValue(ProcessedMap, javaType);
    }

    ProcessedSpat getProcessedSpat() throws JsonProcessingException {
        var mapper = DateJsonMapper.getInstance();
        return mapper.readValue(ProcessedSpat, ProcessedSpat.class);
    }

    ProcessedBsm<Point> getBsm() throws JsonProcessingException  {
        var mapper = DateJsonMapper.getInstance();
        final JavaType bsmGeoJsonType = mapper.getTypeFactory().constructParametricType(ProcessedBsm.class, Point.class);
        return mapper.readValue(BSM, bsmGeoJsonType);
    }



    final String ProcessedMap = "{\"properties\":{\"messageType\":\"MAP\",\"odeReceivedAt\":\"2023-05-03T16:25:03.519Z\",\"originIp\":\"10.11.81.12\",\"intersectionId\":12109,\"msgIssueRevision\":2,\"revision\":2,\"refPoint\":{\"latitude\":39.5880413,\"longitude\":-105.0908854,\"elevation\":1691},\"cti4501Conformant\":false,\"validationMessages\":[{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].id.region: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].id.region\",\"schemaPath\":\"#/$defs/J2735RoadRegulatorID/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[3].connectsTo: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[3].connectsTo\",\"schemaPath\":\"#/$defs/J2735ConnectsToList_Wrapper/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[3].maneuvers: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[3].maneuvers\",\"schemaPath\":\"#/$defs/J2735AllowedManeuvers/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[4].connectsTo: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[4].connectsTo\",\"schemaPath\":\"#/$defs/J2735ConnectsToList_Wrapper/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[4].maneuvers: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[4].maneuvers\",\"schemaPath\":\"#/$defs/J2735AllowedManeuvers/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5].connectsTo: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5].connectsTo\",\"schemaPath\":\"#/$defs/J2735ConnectsToList_Wrapper/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5].maneuvers: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5].maneuvers\",\"schemaPath\":\"#/$defs/J2735AllowedManeuvers/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6].connectsTo: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6].connectsTo\",\"schemaPath\":\"#/$defs/J2735ConnectsToList_Wrapper/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6].maneuvers: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6].maneuvers\",\"schemaPath\":\"#/$defs/J2735AllowedManeuvers/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[8].connectsTo.connectsTo[0].signalGroup: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[8].connectsTo.connectsTo[0].signalGroup\",\"schemaPath\":\"#/$defs/J2735SignalGroupID/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[12].connectsTo.connectsTo[0].signalGroup: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[12].connectsTo.connectsTo[0].signalGroup\",\"schemaPath\":\"#/$defs/J2735SignalGroupID/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[13].connectsTo: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[13].connectsTo\",\"schemaPath\":\"#/$defs/J2735ConnectsToList_Wrapper/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[13].maneuvers: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[13].maneuvers\",\"schemaPath\":\"#/$defs/J2735AllowedManeuvers/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[14].connectsTo: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[14].connectsTo\",\"schemaPath\":\"#/$defs/J2735ConnectsToList_Wrapper/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[14].maneuvers: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[14].maneuvers\",\"schemaPath\":\"#/$defs/J2735AllowedManeuvers/type\"},{\"message\":\"$.payload.data.intersections.intersectionGeometry[0].speedLimits: null found, object expected\",\"jsonPath\":\"$.payload.data.intersections.intersectionGeometry[0].speedLimits\",\"schemaPath\":\"#/$defs/J2735SpeedLimitList_Wrapper/type\"}],\"laneWidth\":366,\"mapSource\":\"RSU\",\"timeStamp\":\"2023-05-03T16:25:03.519Z\"},\"mapFeatureCollection\":{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"id\":1,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0907089,39.587905],[-105.0906245,39.5876246],[-105.0905203,39.587281],[-105.0904383,39.5870554],[-105.0903588,39.5868383],[-105.0902622,39.5865865],[-105.0901249,39.5862612],[-105.0900451,39.5860819],[-105.0899283,39.5858283],[-105.0898739,39.5857117],[-105.0895814,39.5851569],[-105.0888764,39.5839527]]},\"properties\":{\"nodes\":[{\"delta\":[1511,-1514]},{\"delta\":[723,-3116],\"delevation\":10},{\"delta\":[892,-3818],\"delevation\":20},{\"delta\":[702,-2507],\"delevation\":20},{\"delta\":[681,-2412],\"delevation\":10},{\"delta\":[827,-2798],\"delevation\":10},{\"delta\":[1176,-3614],\"delevation\":20},{\"delta\":[683,-1992]},{\"delta\":[1000,-2818],\"delevation\":10},{\"delta\":[466,-1295],\"delevation\":20},{\"delta\":[2505,-6164],\"delevation\":20},{\"delta\":[6037,-13380],\"delevation\":70}],\"laneId\":1,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":1,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":true,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":15,\"maneuver\":{\"maneuverStraightAllowed\":true,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"signalGroup\":2,\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":2,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0907462,39.5878956],[-105.090652,39.5875596],[-105.090534,39.5871793],[-105.0903457,39.5866864],[-105.0902123,39.5863581],[-105.0900802,39.5860572],[-105.0898164,39.5855019],[-105.0895409,39.5849856],[-105.088922,39.5839259]]},\"properties\":{\"nodes\":[{\"delta\":[1192,-1619]},{\"delta\":[807,-3733],\"delevation\":30},{\"delta\":[1010,-4226],\"delevation\":10},{\"delta\":[1612,-5477],\"delevation\":30},{\"delta\":[1142,-3648],\"delevation\":20},{\"delta\":[1131,-3343],\"delevation\":10},{\"delta\":[2259,-6170],\"delevation\":30},{\"delta\":[2359,-5737],\"delevation\":30},{\"delta\":[5300,-11774],\"delevation\":50}],\"laneId\":2,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":1,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":true,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":14,\"maneuver\":{\"maneuverStraightAllowed\":true,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"signalGroup\":2,\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":3,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0907914,39.5878879],[-105.090747,39.5877247],[-105.0906498,39.5874141],[-105.0906262,39.5873356],[-105.0905865,39.5872922]]},\"properties\":{\"nodes\":[{\"delta\":[805,-1704],\"delevation\":10},{\"delta\":[380,-1813]},{\"delta\":[832,-3451],\"delevation\":30},{\"delta\":[202,-872]},{\"delta\":[340,-482],\"delevation\":-10}],\"laneId\":3,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":1,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":true,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":10,\"maneuver\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":true,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"signalGroup\":2,\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":6,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0910008,39.5878477],[-105.0909927,39.5878181]]},\"properties\":{\"nodes\":[{\"delta\":[-988,-2151],\"delevation\":20},{\"delta\":[69,-329]}],\"laneId\":6,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":2,\"ingressApproach\":0,\"ingressPath\":false,\"egressPath\":true}},{\"type\":\"Feature\",\"id\":5,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.090959,39.5878557],[-105.0909501,39.5878218]]},\"properties\":{\"nodes\":[{\"delta\":[-630,-2062],\"delevation\":10},{\"delta\":[76,-377],\"delevation\":10}],\"laneId\":5,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":2,\"ingressApproach\":0,\"ingressPath\":false,\"egressPath\":true}},{\"type\":\"Feature\",\"id\":4,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.090914,39.5878612],[-105.0909051,39.5878298]]},\"properties\":{\"nodes\":[{\"delta\":[-245,-2001],\"delevation\":10},{\"delta\":[76,-349]}],\"laneId\":4,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":2,\"ingressApproach\":0,\"ingressPath\":false,\"egressPath\":true}},{\"type\":\"Feature\",\"id\":10,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0911626,39.5880622],[-105.0912043,39.5880536]]},\"properties\":{\"nodes\":[{\"delta\":[-2374,232],\"delevation\":10},{\"delta\":[-357,-96]}],\"laneId\":10,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":4,\"ingressApproach\":0,\"ingressPath\":false,\"egressPath\":true}},{\"type\":\"Feature\",\"id\":8,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0911477,39.587995],[-105.0914565,39.5879427],[-105.0917937,39.5879029],[-105.0922121,39.5878724],[-105.0926509,39.5878748],[-105.0930303,39.5879073],[-105.0932697,39.5879503],[-105.0937243,39.5880569],[-105.0940309,39.5881258],[-105.0943257,39.5881804],[-105.094592,39.5882097]]},\"properties\":{\"nodes\":[{\"delta\":[-2246,-514],\"delevation\":10},{\"delta\":[-2644,-581]},{\"delta\":[-2887,-442],\"delevation\":10},{\"delta\":[-3583,-339],\"delevation\":10},{\"delta\":[-3757,27]},{\"delta\":[-3249,361],\"delevation\":-10},{\"delta\":[-2050,478]},{\"delta\":[-3893,1184]},{\"delta\":[-2625,766],\"delevation\":-10},{\"delta\":[-2524,607],\"delevation\":10},{\"delta\":[-2280,325],\"delevation\":10}],\"laneId\":8,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":3,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":true,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":15,\"maneuver\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":true,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"signalGroup\":4,\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":7,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0911442,39.5879589],[-105.0914154,39.5879165],[-105.0916346,39.5878851],[-105.0918433,39.5878639],[-105.0921546,39.5878547]]},\"properties\":{\"nodes\":[{\"delta\":[-2216,-915],\"delevation\":10},{\"delta\":[-2322,-471]},{\"delta\":[-1877,-349],\"delevation\":10},{\"delta\":[-1787,-235]},{\"delta\":[-2666,-102],\"delevation\":10}],\"laneId\":7,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":3,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":true,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":6,\"maneuver\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":true,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":9,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0911534,39.5880261],[-105.091436,39.5879812],[-105.0916658,39.5879507],[-105.091881,39.5879277],[-105.0921287,39.5878972]]},\"properties\":{\"nodes\":[{\"delta\":[-2295,-169],\"delevation\":10},{\"delta\":[-2420,-499]},{\"delta\":[-1968,-339],\"delevation\":10},{\"delta\":[-1843,-256]},{\"delta\":[-2121,-339]}],\"laneId\":9,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":3,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":true,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":14,\"maneuver\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":true,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"signalGroup\":4,\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":12,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0910447,39.5881948],[-105.0911481,39.5886317],[-105.091196,39.588862],[-105.0912349,39.5890282],[-105.0912722,39.5893202],[-105.0913306,39.5897261],[-105.0913695,39.5900324],[-105.0914008,39.5903008],[-105.0914893,39.5913099],[-105.091527,39.5923157]]},\"properties\":{\"nodes\":[{\"delta\":[-1364,1705],\"delevation\":10},{\"delta\":[-885,4854],\"delevation\":-30},{\"delta\":[-410,2559],\"delevation\":10},{\"delta\":[-333,1847],\"delevation\":-10},{\"delta\":[-319,3244],\"delevation\":-20},{\"delta\":[-500,4510]},{\"delta\":[-333,3403],\"delevation\":-30},{\"delta\":[-268,2982]},{\"delta\":[-758,11212],\"delevation\":-30},{\"delta\":[-323,11176],\"delevation\":-70}],\"laneId\":12,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":5,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":true,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":5,\"maneuver\":{\"maneuverStraightAllowed\":true,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"signalGroup\":6,\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":13,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0910013,39.5881975],[-105.0911059,39.5886309],[-105.091144,39.5888313],[-105.0911829,39.5890442],[-105.0912308,39.5893169],[-105.0912689,39.5895877],[-105.0913005,39.5898143],[-105.0913313,39.5900714],[-105.0913597,39.5902968],[-105.0914461,39.5913017],[-105.0914756,39.592324]]},\"properties\":{\"nodes\":[{\"delta\":[-992,1735],\"delevation\":10},{\"delta\":[-896,4816],\"delevation\":-30},{\"delta\":[-326,2227],\"delevation\":10},{\"delta\":[-333,2366]},{\"delta\":[-410,3030],\"delevation\":-20},{\"delta\":[-326,3009],\"delevation\":-10},{\"delta\":[-271,2518],\"delevation\":-10},{\"delta\":[-264,2857],\"delevation\":-20},{\"delta\":[-243,2504]},{\"delta\":[-740,11165],\"delevation\":-30},{\"delta\":[-253,11359],\"delevation\":-70}],\"laneId\":13,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":5,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":true,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":4,\"maneuver\":{\"maneuverStraightAllowed\":true,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":false,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"signalGroup\":6,\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":11,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0910891,39.5881859],[-105.0911549,39.5884681],[-105.091196,39.5886783],[-105.091222,39.5888049],[-105.0912401,39.5889649]]},\"properties\":{\"nodes\":[{\"delta\":[-1744,1607],\"delevation\":10},{\"delta\":[-563,3136],\"delevation\":-20},{\"delta\":[-352,2336],\"delevation\":-10},{\"delta\":[-223,1407],\"delevation\":10},{\"delta\":[-155,1778]}],\"laneId\":11,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":0,\"ingressApproach\":5,\"ingressPath\":true,\"egressPath\":false,\"maneuvers\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":true,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false},\"connectsTo\":[{\"connectingLane\":{\"lane\":10,\"maneuver\":{\"maneuverStraightAllowed\":false,\"maneuverNoStoppingAllowed\":false,\"goWithHalt\":false,\"maneuverLeftAllowed\":false,\"maneuverUTurnAllowed\":false,\"maneuverLeftTurnOnRedAllowed\":false,\"reserved1\":false,\"maneuverRightAllowed\":true,\"maneuverLaneChangeAllowed\":false,\"yieldAllwaysRequired\":false,\"maneuverRightTurnOnRedAllowed\":false,\"caution\":false}},\"connectionID\":1}]}},{\"type\":\"Feature\",\"id\":14,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0908389,39.5882151],[-105.0908478,39.5882471]]},\"properties\":{\"nodes\":[{\"delta\":[398,1931],\"delevation\":-10},{\"delta\":[-76,356]}],\"laneId\":14,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":6,\"ingressApproach\":0,\"ingressPath\":false,\"egressPath\":true}},{\"type\":\"Feature\",\"id\":15,\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0907875,39.58822],[-105.0907979,39.5882514]]},\"properties\":{\"nodes\":[{\"delta\":[838,1985],\"delevation\":-20},{\"delta\":[-89,349]}],\"laneId\":15,\"sharedWith\":{\"busVehicleTraffic\":false,\"trackedVehicleTraffic\":false,\"individualMotorizedVehicleTraffic\":false,\"taxiVehicleTraffic\":false,\"overlappingLaneDescriptionProvided\":false,\"cyclistVehicleTraffic\":false,\"otherNonMotorizedTrafficTypes\":false,\"multipleLanesTreatedAsOneLane\":false,\"pedestrianTraffic\":false,\"pedestriansTraffic\":false},\"egressApproach\":6,\"ingressApproach\":0,\"ingressPath\":false,\"egressPath\":true}}]},\"connectingLanesFeatureCollection\":{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"id\":\"1-15\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0907089,39.587905],[-105.0907875,39.58822]]},\"properties\":{\"signalGroupId\":2,\"ingressLaneId\":1,\"egressLaneId\":15}},{\"type\":\"Feature\",\"id\":\"2-14\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0907462,39.5878956],[-105.0908389,39.5882151]]},\"properties\":{\"signalGroupId\":2,\"ingressLaneId\":2,\"egressLaneId\":14}},{\"type\":\"Feature\",\"id\":\"3-10\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0907914,39.5878879],[-105.0911626,39.5880622]]},\"properties\":{\"signalGroupId\":2,\"ingressLaneId\":3,\"egressLaneId\":10}},{\"type\":\"Feature\",\"id\":\"8-15\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0911477,39.587995],[-105.0907875,39.58822]]},\"properties\":{\"signalGroupId\":4,\"ingressLaneId\":8,\"egressLaneId\":15}},{\"type\":\"Feature\",\"id\":\"7-6\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0911442,39.5879589],[-105.0910008,39.5878477]]},\"properties\":{\"ingressLaneId\":7,\"egressLaneId\":6}},{\"type\":\"Feature\",\"id\":\"9-14\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0911534,39.5880261],[-105.0908389,39.5882151]]},\"properties\":{\"signalGroupId\":4,\"ingressLaneId\":9,\"egressLaneId\":14}},{\"type\":\"Feature\",\"id\":\"12-5\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0910447,39.5881948],[-105.090959,39.5878557]]},\"properties\":{\"signalGroupId\":6,\"ingressLaneId\":12,\"egressLaneId\":5}},{\"type\":\"Feature\",\"id\":\"13-4\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0910013,39.5881975],[-105.090914,39.5878612]]},\"properties\":{\"signalGroupId\":6,\"ingressLaneId\":13,\"egressLaneId\":4}},{\"type\":\"Feature\",\"id\":\"11-10\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-105.0910891,39.5881859],[-105.0911626,39.5880622]]},\"properties\":{\"ingressLaneId\":11,\"egressLaneId\":10}}]}}";

    final String ProcessedSpat = "{\"messageType\":\"SPAT\",\"odeReceivedAt\":\"2023-05-03T16:25:04.807Z\",\"originIp\":\"10.11.81.12\",\"intersectionId\":12109,\"cti4501Conformant\":false,\"validationMessages\":[{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].id.region\",\"schemaPath\":\"#/$defs/J2735RoadRegulatorID/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"},{\"message\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime: null found, integer expected\",\"jsonPath\":\"$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime\",\"schemaPath\":\"#/$defs/J2735TimeMark/type\"}],\"revision\":0,\"status\":{\"manualControlIsEnabled\":false,\"stopTimeIsActivated\":false,\"failureFlash\":false,\"preemptIsActive\":false,\"signalPriorityIsActive\":false,\"fixedTimeOperation\":false,\"trafficDependentOperation\":false,\"standbyOperation\":false,\"failureMode\":false,\"off\":false,\"recentMAPmessageUpdate\":false,\"recentChangeInMAPassignedLanesIDsUsed\":false,\"noValidMAPisAvailableAtThisTime\":false,\"noValidSPATisAvailableAtThisTime\":false},\"utcTimeStamp\":\"2023-05-03T16:25:04.807Z\",\"states\":[{\"signalGroup\":2,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]},{\"signalGroup\":4,\"stateTimeSpeed\":[{\"eventState\":\"STOP_AND_REMAIN\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:21.1Z\",\"maxEndTime\":\"2023-05-03T16:03:21.1Z\"}}]},{\"signalGroup\":6,\"stateTimeSpeed\":[{\"eventState\":\"PROTECTED_CLEARANCE\",\"timing\":{\"minEndTime\":\"2023-05-03T16:03:18Z\",\"maxEndTime\":\"2023-05-03T16:03:18Z\"}}]}]}";

    final String BSM = """
            {
                "schemaVersion": 1,
                "features": [
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [
                                -105.09073473997064,
                                39.58785476153264
                            ]
                        },
                        "properties": {
                            "accelSet": {
                                "accelLong": -0.050000000000000003,
                                "accelYaw": -0.65000000000000002
                            },
                            "accuracy": {
                                "semiMajor": 2,
                                "semiMinor": 2,
                                "orientation": 0
                            },
                            "brakes": {
                                "wheelBrakes": {
                                    "leftFront": false,
                                    "rightFront": false,
                                    "unavailable": true,
                                    "leftRear": false,
                                    "rightRear": false
                                },
                                "traction": "unavailable",
                                "abs": "off",
                                "scs": "unavailable",
                                "brakeBoost": "unavailable",
                                "auxBrakes": "unavailable"
                            },
                            "heading": 347.30000000000001,
                            "id": "48C45782",
                            "msgCnt": 12,
                            "secMark": 5988,
                            "size": {
                                "width": 180,
                                "length": 480
                            },
                            "transmission": "UNAVAILABLE"
                        }
                    }
                ],
                "messageType": "BSM",
                "odeReceivedAt": "2023-05-03T16:25:05.988Z",
                "timeStamp": "2023-05-03T16:25:05.988Z",
                "originIp": "10.11.81.12",
                "validationMessages": [
                ],
                "type": "FeatureCollection"
            }
            """;


}
