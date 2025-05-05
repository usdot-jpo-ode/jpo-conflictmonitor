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



public class VehicleMisbehaviorTopologyTest {
    String bsmInputTopicName = "topic.ProcessedBsm";
    String outputEventTopicName = "topic.CmVehicleMisbehaviorEvents";

    String processedBsm1 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:07.723Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":8.54,\"transmission\":\"FORWARDGEARS\"}}";
    String processedBsm2 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.823Z\",\"timeStamp\":\"2025-04-14T17:27:07.723Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":8.54,\"transmission\":\"FORWARDGEARS\"}}";


    // String bsmKey = "{\"rsuId\":\"10.164.6.18\",\"logId\":\"\",\"bsmId\":\"492A23D0\"}";
    BsmRsuIdKey key = new BsmRsuIdKey("10.164.6.18", "492A23D0");

    long processedBsm1Timestamp = 1744650797477L;
    long processedBsm2Timestamp = 1744650797477L + 100;

    DateJsonMapper objectMapper;

    TypeReference<ProcessedBsm<Point>> bsmType = new TypeReference<ProcessedBsm<Point>>(){};

    @Test
    public void testTopology() {

        VehicleMisbehaviorTopology misbehaviorTopology = new VehicleMisbehaviorTopology();
        VehicleMisbehaviorParameters parameters = new VehicleMisbehaviorParameters();

        parameters.setAlgorithm("defaultVehicleMisbehaviorAlgorithm");
        parameters.setDebug(true);
        parameters.setAccelerationRange(10);
        parameters.setSpeedRange(10);
        parameters.setYawRateRange(10);
        parameters.setProcessedBsmStateStoreName("processedBsmVehicleMisbehaviorStateStore");
        parameters.setBsmInputTopicName(bsmInputTopicName);
        parameters.setVehicleMisbehaviorEventOutputTopicName(outputEventTopicName);


        misbehaviorTopology.setParameters(parameters);

        Topology topology = misbehaviorTopology.buildTopology();

        objectMapper = new DateJsonMapper();

        // final String rsuId = "127.0.0.1";
        // final int region = 0;
        // final int intersectionId = 12109;
        // final int signalGroup = 5;
        // final J2735MovementPhaseState phaseState = J2735MovementPhaseState.STOP_AND_REMAIN;
        // final long timestamp1 = 1732215600000L; // Top of the hour
        // final long timestamp2 = timestamp1 + 10L;
        // final long timeMark1 = 5;
        // final long timeMark2 = 3;
        // // TimeMarks are 1/10 of a second relative to top of the hour, so millis = TimeMark * 100
        // final long minEndTime1 = timestamp1 + timeMark1 * 100;
        // final long minEndTime2 = timestamp1 + timeMark2 * 100;



        // final var spat1 = spat(timestamp1, region, intersectionId, signalGroup, phaseState, minEndTime1);
        // System.out.println(spat1);
        // final var spat2 = spat(timestamp2, region, intersectionId, signalGroup, phaseState, minEndTime2);
        // System.out.println(spat2);
        // final var key = new RsuIntersectionKey(rsuId, intersectionId, region);
        // System.out.println(key);






        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {

            var inputProcessedBsmTopic = driver.createInputTopic(
                    parameters.getBsmInputTopicName(),
                    JsonSerdes.BsmRsuIdKey().serializer(),
                    Serdes.String().serializer()
            );

            var outputEventTopic = driver.createOutputTopic(
                    parameters.getVehicleMisbehaviorEventOutputTopicName(),
                    JsonSerdes.BsmRsuIdKey().deserializer(),
                    JsonSerdes.VehicleMisbehaviorEvent().deserializer()
            );

            

            // ProcessedBsm<Point> bsm1 = objectMapper.readValue(processedBsm1, bsmType);
            // ProcessedBsm<Point> bsm2 = objectMapper.readValue(processedBsm2, bsmType);
            
            inputProcessedBsmTopic.pipeInput(key, processedBsm1);
            inputProcessedBsmTopic.pipeInput(key, processedBsm2);

            final var eventResults = outputEventTopic.readKeyValuesToList();
            // assertEquals(1, eventResults.size());
            // final var eventResult = eventResults.getFirst();
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