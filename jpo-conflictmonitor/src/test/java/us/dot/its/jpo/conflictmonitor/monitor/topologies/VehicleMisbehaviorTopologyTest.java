package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import com.fasterxml.jackson.core.type.TypeReference;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.vehicle_misbehavior.VehicleMisbehaviorParameters;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class VehicleMisbehaviorTopologyTest {
    String bsmInputTopicName = "topic.ProcessedBsm";
    String outputEventTopicName = "topic.CmVehicleMisbehaviorEvents";

    // Group 1, No event should be generated. Mostly identical BSMs
    String noEventBsm1 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.000Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":0,\"transmission\":\"FORWARDGEARS\"}}";
    String noEventBsm2 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.823Z\",\"timeStamp\":\"2025-04-14T17:27:01.100Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":0,\"transmission\":\"FORWARDGEARS\"}}";
    
    // Group 2 Speed Event should be generated because vehicle is moving too fast.
    String speedEventBsm1 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.000Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":253,\"transmission\":\"FORWARDGEARS\"}}";
    
    
    
    String processedBsm3 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.823Z\",\"timeStamp\":\"2025-04-14T17:27:01.200Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":8.54,\"transmission\":\"FORWARDGEARS\"}}";


    // String bsmKey = "{\"rsuId\":\"10.164.6.18\",\"logId\":\"\",\"bsmId\":\"492A23D0\"}";
    BsmRsuIdKey key = new BsmRsuIdKey("10.164.6.18", "492A23D0");

    long processedBsm1Timestamp = 1744650797477L;
    long processedBsm2Timestamp = 1744650797477L + 100;

    DateJsonMapper objectMapper = new DateJsonMapper();

    TypeReference<ProcessedBsm<Point>> bsmType = new TypeReference<ProcessedBsm<Point>>(){};


    public Topology getTopology(){
        VehicleMisbehaviorTopology misbehaviorTopology = new VehicleMisbehaviorTopology();
        VehicleMisbehaviorParameters parameters = new VehicleMisbehaviorParameters();

        parameters.setAlgorithm("defaultVehicleMisbehaviorAlgorithm");
        parameters.setDebug(true);
        parameters.setAccelerationRangeLateral(13.1);
        parameters.setAccelerationRangeLongitudinal(39.4);
        parameters.setAccelerationRangeVertical(13.1);
        parameters.setSpeedRange(2);
        parameters.setYawRateRange(10);
        parameters.setAllowableMaxHeadingDelta(85.9);
        parameters.setAllowableMaxSpeed(156.5);
        parameters.setProcessedBsmStateStoreName("processedBsmVehicleMisbehaviorStateStore");
        parameters.setBsmInputTopicName(bsmInputTopicName);
        parameters.setVehicleMisbehaviorEventOutputTopicName(outputEventTopicName);


        misbehaviorTopology.setParameters(parameters);

        return misbehaviorTopology.buildTopology();
    }


    @Test
    public void testNoEventGeneration() {

        Topology topology = getTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {

            var inputProcessedBsmTopic = driver.createInputTopic(
                bsmInputTopicName,
                    JsonSerdes.BsmRsuIdKey().serializer(),
                    Serdes.String().serializer()
            );

            var outputEventTopic = driver.createOutputTopic(
                    outputEventTopicName,
                    JsonSerdes.BsmRsuIdKey().deserializer(),
                    JsonSerdes.VehicleMisbehaviorEvent().deserializer()
            );
            
            inputProcessedBsmTopic.pipeInput(key, noEventBsm1);
            inputProcessedBsmTopic.pipeInput(key, noEventBsm2);

            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(0, eventResults.size());
        }
    }

    @Test
    public void testSpeedEventGeneration() {

        Topology topology = getTopology();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {

            var inputProcessedBsmTopic = driver.createInputTopic(
                bsmInputTopicName,
                    JsonSerdes.BsmRsuIdKey().serializer(),
                    Serdes.String().serializer()
            );

            var outputEventTopic = driver.createOutputTopic(
                    outputEventTopicName,
                    JsonSerdes.BsmRsuIdKey().deserializer(),
                    JsonSerdes.VehicleMisbehaviorEvent().deserializer()
            );

            

            // ProcessedBsm<Point> bsm1 = objectMapper.readValue(processedBsm1, bsmType);
            // ProcessedBsm<Point> bsm2 = objectMapper.readValue(processedBsm2, bsmType);
            
            inputProcessedBsmTopic.pipeInput(key, speedEventBsm1);

            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            final var eventResult = eventResults.getFirst();
            assertEquals(Math.round(eventResult.value.getReportedSpeed()), 157);
            // final var resultKey = eventResult.key;
            // final var resultValue = eventResult.value;
            // System.out.printf("result eventKey: %s, eventValue: %s%n", resultKey, resultValue);
            // assertEquals(rsuId, resultKey.getRsuId());
            // assertEquals(intersectionId, resultKey.getIntersectionId());
            // assertEquals(region, resultKey.getRegion());
            // assertEquals(signalGroup, resultKey.getSignalGroup());

            // assertEquals(timeMark1, resultValue.getFirstConflictingTimemark());
            // assertEquals(timeMark2, resultValue.getSecondConflictingTimemark());


            // List<KeyValue<String, TimeChangeDetailsNotification>> notificationResults = outputNotificationTopic.readKeyValuesToList();
            // assertEquals(1, notificationResults.size());

            // KeyValue<String, TimeChangeDetailsNotification> notificationKeyValue = notificationResults.getFirst();

            // assertNotNull(notificationKeyValue.key);


            // TimeChangeDetailsNotification notification = notificationKeyValue.value;

            // assertEquals("TimeChangeDetailsNotification", notification.getNotificationType());

            // assertEquals("Time Change Details Notification, generated because corresponding time change details event was generated.", notification.getNotificationText());

            // assertEquals("Time Change Details", notification.getNotificationHeading());

        }
    }

    // private ProcessedSpat spat(
    //         final long utcTimestamp,
    //         final int region,
    //         final int intersectionId,
    //         final int signalGroup,
    //         final J2735MovementPhaseState phaseState,
    //         final long minEndTime) {

    //     final var spat1 = new ProcessedSpat();
    //     spat1.setUtcTimeStamp(zdt(utcTimestamp));
    //     spat1.setRegion(region);
    //     spat1.setIntersectionId(intersectionId);

    //     final var state1 = new MovementState();
    //     state1.setSignalGroup(signalGroup);

    //     final var event1 = new MovementEvent();
    //     event1.setEventState(phaseState);

    //     final var timing1 = new TimingChangeDetails();
    //     timing1.setMinEndTime(zdt(minEndTime));
    //     event1.setTiming(timing1);

    //     state1.setStateTimeSpeed(Lists.newArrayList(event1));

    //     spat1.setStates(Lists.newArrayList(state1));

    //     return spat1;
    // }

    // private ZonedDateTime zdt(final long timestamp) {
    //     return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
    // }
}


