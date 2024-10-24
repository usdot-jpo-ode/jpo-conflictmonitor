package us.dot.its.jpo.conflictmonitor.monitor.notifications;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.TimeChangeDetailsEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.TimeChangeDetailsNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.time_change_details.SpatTimeChangeDetailsNotificationTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class TimeChangeDetailsNotificationTopologyTest {
    String spatTimeChangeDetailsEventTopicName = "topic.CmSpatTimeChangeDetailsEvent";
    String spatTimeChangeDetailsNotificationTopicName = "topic.CmSpatTimeChangeDetailsNotification";
    @Test
    public void testTopology() {

        SpatTimeChangeDetailsNotificationTopology spatTopology = new SpatTimeChangeDetailsNotificationTopology();
        SpatTimeChangeDetailsParameters parameters = new SpatTimeChangeDetailsParameters();

        parameters.setDebug(false);
        parameters.setSpatTimeChangeDetailsTopicName(spatTimeChangeDetailsEventTopicName);
        parameters.setSpatTimeChangeDetailsNotificationTopicName(spatTimeChangeDetailsNotificationTopicName);


        spatTopology.setParameters(parameters);

        Topology topology = spatTopology.buildTopology();

        TimeChangeDetailsEvent inputEvent = new TimeChangeDetailsEvent();
        inputEvent.setFirstConflictingTimemark(10);
        inputEvent.setIntersectionID(12109);
        inputEvent.setRoadRegulatorID(0);
        inputEvent.setSignalGroup(5);
        inputEvent.setFirstSpatTimestamp(0);
        inputEvent.setSecondSpatTimestamp(10);
        inputEvent.setFirstTimeMarkType("minEndTime");
        inputEvent.setSecondTimeMarkType("minEndTime");
        inputEvent.setFirstConflictingTimemark(0);
        inputEvent.setSecondConflictingTimemark(10);
        

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<String, TimeChangeDetailsEvent> inputTimeChangeDetailsEventTopic = driver.createInputTopic(
                spatTimeChangeDetailsEventTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.TimeChangeDetailsEvent().serializer());

            TestOutputTopic<String, TimeChangeDetailsNotification> outputNotificationTopic = driver.createOutputTopic(
                spatTimeChangeDetailsNotificationTopicName, 
                Serdes.String().deserializer(), 
                JsonSerdes.TimeChangeDetailsNotification().deserializer());
            
            inputTimeChangeDetailsEventTopic.pipeInput("12109", inputEvent);


            
            


            List<KeyValue<String, TimeChangeDetailsNotification>> notificationResults = outputNotificationTopic.readKeyValuesToList();
            assertEquals(1, notificationResults.size());
 
            KeyValue<String, TimeChangeDetailsNotification> notificationKeyValue = notificationResults.get(0);

            assertEquals("12109", notificationKeyValue.key);

            TimeChangeDetailsNotification notification = notificationKeyValue.value;

            assertEquals("TimeChangeDetailsNotification", notification.getNotificationType());

            assertEquals("Time Change Details Notification, generated because corresponding time change details event was generated.", notification.getNotificationText());

            assertEquals("Time Change Details", notification.getNotificationHeading());

            TimeChangeDetailsEvent event = notification.getEvent();
            assertEquals(event, inputEvent);
            
            
        }
        assertEquals(0,0);
    }
}