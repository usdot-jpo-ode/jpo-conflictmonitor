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
    
    // Group 2 Vehicle Misbehavior Event should be generated because vehicle is moving too fast.
    String speedEventBsm1 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.000Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":253,\"transmission\":\"FORWARDGEARS\"}}";

    // Group 3 Vehicle Misbehavior Event should be generated because the calculated and actual speeds do not match.
    String mismatchedSpeedEventBsm1 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.000Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":25,\"transmission\":\"FORWARDGEARS\"}}";
    String mismatchedSpeedEventBsm2 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.823Z\",\"timeStamp\":\"2025-04-14T17:27:01.100Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":25,\"transmission\":\"FORWARDGEARS\"}}";

    // Group 4 Vehicle Misbehavior Event should be generated because the Yaw Rate is too high. 
    String headingEventBsm1 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.000Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":690},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":0,\"transmission\":\"FORWARDGEARS\"}}";
  
    // Group 5 Vehicle Misbehavior Event should be generated because Heading of Vehicle does not match the calculated yaw rate
    String mismatchedHeadingEventBsm1 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.000Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":0,\"transmission\":\"FORWARDGEARS\"}}";
    String mismatchedHeadingEventBsm2 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.823Z\",\"timeStamp\":\"2025-04-14T17:27:01.100Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":25,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":0,\"transmission\":\"FORWARDGEARS\"}}";
    
    // Group 6 Vehicle Misbehavior Event should be generated because accelerations are too high.
    String accelerationEventBsm1 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.000Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":50,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":0,\"transmission\":\"FORWARDGEARS\"}}";
    String accelerationEventBsm2 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.100Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":50,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":0,\"transmission\":\"FORWARDGEARS\"}}";
    String accelerationEventBsm3 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.723Z\",\"timeStamp\":\"2025-04-14T17:27:01.200Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":50,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":0,\"transmission\":\"FORWARDGEARS\"}}";

    // String processedBsm3 = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-111.6919827,40.2972862]},\"properties\":{\"schemaVersion\":6,\"messageType\":\"BSM\",\"odeReceivedAt\":\"2025-04-14T17:27:07.823Z\",\"timeStamp\":\"2025-04-14T17:27:01.200Z\",\"originIp\":\"10.164.6.18\",\"validationMessages\":[{\"message\":\"$.metadata.schemaVersion: must be a constant value 8\",\"jsonPath\":\"$.metadata.schemaVersion\",\"schemaPath\":\"#/properties/metadata/properties/schemaVersion/const\"},{\"message\":\"$.metadata.asn1: is missing but it is required\",\"jsonPath\":\"$.metadata\",\"schemaPath\":\"#/properties/metadata/required\"}],\"accelSet\":{\"accelLat\":2001,\"accelLong\":2001,\"accelVert\":-127,\"accelYaw\":0},\"accuracy\":{\"semiMajor\":2,\"semiMinor\":2,\"orientation\":44.49530799},\"angle\":10.5,\"brakes\":{\"wheelBrakes\":{\"leftFront\":false,\"rightFront\":false,\"unavailable\":true,\"leftRear\":false,\"rightRear\":false},\"traction\":\"on\",\"abs\":\"on\",\"scs\":\"on\",\"brakeBoost\":\"off\",\"auxBrakes\":\"unavailable\"},\"heading\":21.2,\"id\":\"6F2875C1\",\"msgCnt\":115,\"secMark\":7723,\"size\":{\"width\":230,\"length\":500},\"speed\":8.54,\"transmission\":\"FORWARDGEARS\"}}";


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
    public void testMismatchedSpeedEventGeneration() {

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
            
            inputProcessedBsmTopic.pipeInput(key, mismatchedSpeedEventBsm1);
            inputProcessedBsmTopic.pipeInput(key, mismatchedSpeedEventBsm2);

            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            final var eventResult = eventResults.getFirst();
            assertEquals(Math.round(eventResult.value.getCalculatedSpeed()), 0);
            assertEquals(Math.round(eventResult.value.getReportedSpeed()), 16);
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
            
            inputProcessedBsmTopic.pipeInput(key, speedEventBsm1);

            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            final var eventResult = eventResults.getFirst();
            assertEquals(Math.round(eventResult.value.getReportedSpeed()), 157);

        }
    }

    @Test
    public void testMismatchedHeadingEventGeneration() {

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
            
            inputProcessedBsmTopic.pipeInput(key, mismatchedHeadingEventBsm1);
            inputProcessedBsmTopic.pipeInput(key, mismatchedHeadingEventBsm2);

            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            final var eventResult = eventResults.getFirst();
            assertEquals(Math.round(eventResult.value.getReportedYawRate()), 0);
            assertEquals(Math.round(eventResult.value.getCalculatedYawRate()), 38);
        }
    }

    @Test
    public void testHeadingEventGeneration() {

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
            
            inputProcessedBsmTopic.pipeInput(key, headingEventBsm1);

            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            final var eventResult = eventResults.getFirst();
            assertEquals(Math.round(eventResult.value.getReportedYawRate()), 690);
        }
    }

    @Test
    public void testAccelerationLatEventGeneration() {

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
            
            inputProcessedBsmTopic.pipeInput(key, accelerationEventBsm1);


            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            assertEquals(Math.round(eventResults.get(0).value.getReportedAccelerationLat()), 164);
        }
    }

    @Test
    public void testAccelerationLonEventGeneration() {

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
            
            inputProcessedBsmTopic.pipeInput(key, accelerationEventBsm2);


            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            assertEquals(Math.round(eventResults.get(0).value.getReportedAccelerationLon()), 164);
        }
    }

    @Test
    public void testAccelerationVertEventGeneration() {

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
            
            inputProcessedBsmTopic.pipeInput(key, accelerationEventBsm3);


            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            assertEquals(Math.round(eventResults.get(0).value.getReportedAccelerationVert()), 164);
        }
    }
}


