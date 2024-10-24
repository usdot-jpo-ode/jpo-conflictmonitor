package us.dot.its.jpo.conflictmonitor.monitor.events;


import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import us.dot.its.jpo.conflictmonitor.monitor.models.events.*;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.MapBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.broadcast_rate.SpatBroadcastRateEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.MapMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.MapTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.timestamp_delta.SpatTimestampDeltaEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.EventTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.event.EventParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class EventTopologyTest {
    String eventOutputTopicName = "topic.CmEvent";
    String signalStateEventTopicName = "topic.CmSignalStateEvent";
    String spatTimeChangeDetailsTopicName = "topic.CmSpatTimeChangeDetailsEvent";
    String spatBroadcastRateTopicName = "topic.CmSpatBroadcastRateEvents";
    String spatMinimumDataTopicName = "topic.CmSpatMinimumDataEvents";
    String mapBroadcastRateTopicName = "topic.CmMapBroadcastRateEvents";
    String mapMinimumDataTopicName = "topic.CmMapMinimumDataEvents";
    String signalGroupAlignmentEventTopicName = "topic.CmSignalGroupAlignmentEvents";
    String intersectionReferenceAlignmentEventTopicName = "topic.CmIntersectionReferenceAlignmentEvents";
    String signalStateConflictEventTopicName = "topic.CmSignalStateConflictEvents";
    String laneDirectionOfTravelEventTopicName = "topic.CmLaneDirectionOfTravelEvent";
    String connectionOfTravelEventTopicName = "topic.CmConnectionOfTravelEvent";
    String spatRevisionCounterEventTopicName = "topic.CmSpatRevisionCounterEvents";
    String mapRevisionCounterEventTopicName = "topic.CmMapRevisionCounterEvents";
    String bsmRevisionCounterEventTopicName = "topic.CmBsmRevisionCounterEvents";
    String timestampDeltaEventTopicName = "topic.CmTimestampDeltaEvent";
    String spatTransitionEventTopicName = "topic.CmEventStateProgressionEvent";

    @Test
    public void testTopology() {

        EventTopology eventTopology = new EventTopology();
        EventParameters parameters = new EventParameters();
        parameters.setEventOutputTopicName(eventOutputTopicName);
        parameters.setSignalStateEventTopicName(signalStateEventTopicName);
        parameters.setSpatTimeChangeDetailsTopicName(spatTimeChangeDetailsTopicName);
        parameters.setSpatBroadcastRateTopicName(spatBroadcastRateTopicName);
        parameters.setSpatMinimumDataTopicName(spatMinimumDataTopicName);
        parameters.setMapBroadcastRateTopicName(mapBroadcastRateTopicName);
        parameters.setMapMinimumDataTopicName(mapMinimumDataTopicName);
        parameters.setSignalGroupAlignmentEventTopicName(signalGroupAlignmentEventTopicName);
        parameters.setIntersectionReferenceAlignmentEventTopicName(intersectionReferenceAlignmentEventTopicName);
        parameters.setSignalStateConflictEventTopicName(signalStateConflictEventTopicName);
        parameters.setLaneDirectionOfTravelEventTopicName(laneDirectionOfTravelEventTopicName);
        parameters.setConnectionOfTravelEventTopicName(connectionOfTravelEventTopicName);
        parameters.setSpatRevisionCounterEventTopicName(spatRevisionCounterEventTopicName);
        parameters.setMapRevisionCounterEventTopicName(mapRevisionCounterEventTopicName);
        parameters.setBsmRevisionCounterEventTopicName(bsmRevisionCounterEventTopicName);
        parameters.setTimestampDeltaEventTopicName(timestampDeltaEventTopicName);
        parameters.setEventStateProgressionEventTopicName(spatTransitionEventTopicName);

        eventTopology.setParameters(parameters);

        Topology topology = eventTopology.buildTopology();


        StopLinePassageEvent ssEvent = new StopLinePassageEvent();
        TimeChangeDetailsEvent stcdEvent = new TimeChangeDetailsEvent();
        SpatBroadcastRateEvent sbrEvent = new SpatBroadcastRateEvent();
        SpatMinimumDataEvent smdEvent = new SpatMinimumDataEvent();
        MapBroadcastRateEvent mbrEvent = new MapBroadcastRateEvent();
        MapMinimumDataEvent mmdEvent = new MapMinimumDataEvent();
        SignalGroupAlignmentEvent sgaEvent = new SignalGroupAlignmentEvent();
        IntersectionReferenceAlignmentEvent iraEvent = new IntersectionReferenceAlignmentEvent();
        SignalStateConflictEvent sscEvent = new SignalStateConflictEvent();
        LaneDirectionOfTravelEvent ldotEvent = new LaneDirectionOfTravelEvent();
        ConnectionOfTravelEvent cotEvent = new ConnectionOfTravelEvent();
        SpatRevisionCounterEvent srcEvent = new SpatRevisionCounterEvent();
        MapRevisionCounterEvent mrcEvent = new MapRevisionCounterEvent();
        BsmRevisionCounterEvent brcEvent = new BsmRevisionCounterEvent();
        MapTimestampDeltaEvent mapTimestampDeltaEvent = new MapTimestampDeltaEvent();
        SpatTimestampDeltaEvent spatTimestampDeltaEvent = new SpatTimestampDeltaEvent();
        EventStateProgressionEvent spatTransitionEvent = new EventStateProgressionEvent();
        

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            TestInputTopic<String, StopLinePassageEvent> inputSignalState = driver.createInputTopic(
                signalStateEventTopicName, 
                Serdes.String().serializer(),
                JsonSerdes.StopLinePassageEvent().serializer());

            inputSignalState.pipeInput("12109", ssEvent);

            TestInputTopic<String, TimeChangeDetailsEvent> inputTimeChangeDetailsEvent = driver.createInputTopic(
                spatTimeChangeDetailsTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.TimeChangeDetailsEvent().serializer());

            inputTimeChangeDetailsEvent.pipeInput("12109", stcdEvent);

            TestInputTopic<String, SpatBroadcastRateEvent> inputSpatBroadcastRateEvent = driver.createInputTopic(
                spatBroadcastRateTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.SpatBroadcastRateEvent().serializer());

            inputSpatBroadcastRateEvent.pipeInput("12109", sbrEvent);

            TestInputTopic<String, SpatMinimumDataEvent> inputSpatMinimumDataEvent = driver.createInputTopic(
                spatMinimumDataTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.SpatMinimumDataEvent().serializer());

            inputSpatMinimumDataEvent.pipeInput("12109", smdEvent);

            TestInputTopic<String, MapBroadcastRateEvent> inputMapBroadcastRateEvent = driver.createInputTopic(
                mapMinimumDataTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.MapBroadcastRateEvent().serializer());

            inputMapBroadcastRateEvent.pipeInput("12109", mbrEvent);

            TestInputTopic<String, MapMinimumDataEvent> inputMapMinimumDataEvent = driver.createInputTopic(
                mapMinimumDataTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.MapMinimumDataEvent().serializer());

            inputMapMinimumDataEvent.pipeInput("12109", mmdEvent);

            TestInputTopic<String, SignalGroupAlignmentEvent> inputSignalGroupAlignmentEvent = driver.createInputTopic(
                signalGroupAlignmentEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.SignalGroupAlignmentEvent().serializer());

            inputSignalGroupAlignmentEvent.pipeInput("12109", sgaEvent);

            TestInputTopic<String, IntersectionReferenceAlignmentEvent> inputIntersectionReferenceAlignmentEvent = driver.createInputTopic(
                intersectionReferenceAlignmentEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.IntersectionReferenceAlignmentEvent().serializer());

            inputIntersectionReferenceAlignmentEvent.pipeInput("12109", iraEvent);

            TestInputTopic<String, SignalStateConflictEvent> inputSignalStateConflictEvent = driver.createInputTopic(
                intersectionReferenceAlignmentEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.SignalStateConflictEvent().serializer());

            inputSignalStateConflictEvent.pipeInput("12109", sscEvent);

            TestInputTopic<String, LaneDirectionOfTravelEvent> inputLaneDirectionOfTravel = driver.createInputTopic(
                laneDirectionOfTravelEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.LaneDirectionOfTravelEvent().serializer());

            inputLaneDirectionOfTravel.pipeInput("12109", ldotEvent);

            TestInputTopic<String, ConnectionOfTravelEvent> inputConnectionOfTravel = driver.createInputTopic(
                connectionOfTravelEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.ConnectionOfTravelEvent().serializer());

            inputConnectionOfTravel.pipeInput("12109", cotEvent);
            
            TestInputTopic<String, SpatRevisionCounterEvent> inputSpatRevisionCounterEvent = driver.createInputTopic(
                spatRevisionCounterEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.SpatRevisionCounterEvent().serializer());

            inputSpatRevisionCounterEvent.pipeInput("12109", srcEvent);

            TestInputTopic<String, MapRevisionCounterEvent> inputMapRevisionCounterEvent = driver.createInputTopic(
                mapRevisionCounterEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.MapRevisionCounterEvent().serializer());

            inputMapRevisionCounterEvent.pipeInput("12109", mrcEvent);

            TestInputTopic<String, BsmRevisionCounterEvent> inputBsmRevisionCounterEvent = driver.createInputTopic(
                bsmRevisionCounterEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.BsmRevisionCounterEvent().serializer());

            inputBsmRevisionCounterEvent.pipeInput("12109", brcEvent);

            TestInputTopic<String, Event> inputTimestampDeltaEvent = driver.createInputTopic(
                    timestampDeltaEventTopicName,
                    Serdes.String().serializer(),
                    JsonSerdes.Event().serializer());
            inputTimestampDeltaEvent.pipeInput("12109", mapTimestampDeltaEvent);
            inputTimestampDeltaEvent.pipeInput("12109", spatTimestampDeltaEvent);

            TestInputTopic<String, Event> inputSpatTransitionEvent = driver.createInputTopic(
              spatTransitionEventTopicName,
              Serdes.String().serializer(),
              JsonSerdes.Event().serializer());
            inputSpatTransitionEvent.pipeInput("12109", spatTransitionEvent);

            TestOutputTopic<String, Event> outputEventTopic = driver.createOutputTopic(
                eventOutputTopicName, 
                Serdes.String().deserializer(), 
                JsonSerdes.Event().deserializer());


            List<KeyValue<String, Event>> eventResults = outputEventTopic.readKeyValuesToList();

            
            
            assertEquals(17, eventResults.size());
 
            for(KeyValue<String, Event> eventKeyValue: eventResults){
                assertEquals("12109", eventKeyValue.key);
                Event event = eventKeyValue.value;
                String type = event.getEventType();
                System.out.println(type);
                if(type.equals("StopLinePassage")){
                    assertEquals((StopLinePassageEvent) event, ssEvent);
                }
                else if(type.equals("TimeChangeDetails")){
                    assertEquals((TimeChangeDetailsEvent) event, stcdEvent);
                }
                else if(type.equals("SpatBroadcastRate")){
                    assertEquals((SpatBroadcastRateEvent) event, sbrEvent);
                }
                else if(type.equals("SpatMinimumData")){
                    assertEquals((SpatMinimumDataEvent) event, smdEvent);
                }
                else if(type.equals("MapBroadcastRate")){
                    assertEquals((MapBroadcastRateEvent) event, mbrEvent);
                }
                else if(type.equals("MapMinimumData")){
                    assertEquals((MapMinimumDataEvent) event, mmdEvent);
                }
                else if(type.equals("SignalGroupAlignment")){
                    assertEquals((SignalGroupAlignmentEvent) event, sgaEvent);
                }
                else if(type.equals("IntersectionReferenceAlignment")){
                    assertEquals((IntersectionReferenceAlignmentEvent) event, iraEvent);
                }
                else if(type.equals("SignalStateConflict")){
                    assertEquals((SignalStateConflictEvent) event, sscEvent);
                }
                else if(type.equals("LaneDirectionOfTravel")){
                    System.out.println(ldotEvent);
                    assertEquals((LaneDirectionOfTravelEvent) event, ldotEvent);
                }
                else if(type.equals("ConnectionOfTravel")){
                    assertEquals((ConnectionOfTravelEvent) event, cotEvent);
                }
                else if(type.equals("SpatRevisionCounter")){
                    assertEquals((SpatRevisionCounterEvent) event, srcEvent);
                }
                else if(type.equals("MapRevisionCounter")){
                    assertEquals((MapRevisionCounterEvent) event, mrcEvent);
                }
                else if(type.equals("BsmRevisionCounter")){
                    assertEquals((BsmRevisionCounterEvent) event, brcEvent);
                }
                else if (type.equals("MapTimestampDelta")) {
                    assertEquals(event, mapTimestampDeltaEvent);
                }
                else if (type.equals("SpatTimestampDelta")) {
                    assertEquals(event, spatTimestampDeltaEvent);
                } else if (type.equals("EventStateProgression")) {
                    assertEquals(event, spatTransitionEvent);
                }
                else{
                    // Throw an error
                    System.out.println("Unrecognized Type for Decoding: " + type);
                    assertEquals(1,0);
                }
            }            
        }
        assertEquals(0,0);
    }
}