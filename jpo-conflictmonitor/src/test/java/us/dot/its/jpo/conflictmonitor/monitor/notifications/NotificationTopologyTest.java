package us.dot.its.jpo.conflictmonitor.monitor.notifications;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;

import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.ConnectionOfTravelNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.IntersectionReferenceAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.LaneDirectionOfTravelNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.Notification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.SignalGroupAlignmentNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.SignalStateConflictNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.TimeChangeDetailsNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.MapTimestampDeltaNotification;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.timestamp_delta.SpatTimestampDeltaNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.topologies.NotificationTopology;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.notification.NotificationParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class NotificationTopologyTest {
    String connectionOfTravelTopicName = "topic.CmConnectionOfTravelNotification";
    String laneDirectionOfTravelTopicName = "topic.CmLaneDirectionOfTravelNotification";
    String intersectionReferenceAlignmentTopicName = "topic.CmIntersectionReferenceAlignmentNotification";
    String signalGroupAlignmentTopicName = "topic.CmSignalGroupAlignmentNotifications";
    String signalStateConflictTopicName = "topic.CmSignalStateConflictNotification";
    String spatTimeChangeDetailsTopicName = "topic.CmSpatTimeChangeDetailsNotification";
    String notificationTopicName = "topic.CmNotification";
    String timestampDeltaNotificationTopicName = "topic.CmTimestampDeltaNotification";


    @Test
    public void testTopology() {

        NotificationTopology notificationTopology = new NotificationTopology();
        NotificationParameters parameters = new NotificationParameters();
        parameters.setConnectionOfTravelNotificationTopicName(connectionOfTravelTopicName);
        parameters.setLaneDirectionOfTravelNotificationTopicName(laneDirectionOfTravelTopicName);
        parameters.setSignalGroupAlignmentNotificationTopicName(intersectionReferenceAlignmentTopicName);
        parameters.setIntersectionReferenceAlignmentNotificationTopicName(signalGroupAlignmentTopicName);
        parameters.setSignalStateConflictNotificationTopicName(signalStateConflictTopicName);
        parameters.setSpatTimeChangeDetailsNotificationTopicName(spatTimeChangeDetailsTopicName);
        parameters.setNotificationOutputTopicName(notificationTopicName);
        parameters.setDebug(false);
        parameters.setTimestampDeltaNotificationTopicName(timestampDeltaNotificationTopicName);
        


        notificationTopology.setParameters(parameters);

        Topology topology = notificationTopology.buildTopology();

        ConnectionOfTravelNotification cotNotification = new ConnectionOfTravelNotification();
        LaneDirectionOfTravelNotification ldotNotification = new LaneDirectionOfTravelNotification();
        IntersectionReferenceAlignmentNotification iraNotification = new IntersectionReferenceAlignmentNotification();
        SignalGroupAlignmentNotification sgaNotification = new SignalGroupAlignmentNotification();
        SignalStateConflictNotification sscNotification = new SignalStateConflictNotification();
        TimeChangeDetailsNotification tcdNotification = new TimeChangeDetailsNotification();
        MapTimestampDeltaNotification mapTimestampDeltaNotification = new MapTimestampDeltaNotification();
        SpatTimestampDeltaNotification spatTimestampDeltaNotification = new SpatTimestampDeltaNotification();

        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {
            
            
            TestInputTopic<String, ConnectionOfTravelNotification> inputConnectionOfTravel = driver.createInputTopic(
                connectionOfTravelTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.ConnectionOfTravelNotification().serializer());

            inputConnectionOfTravel.pipeInput("12109", cotNotification);

            TestInputTopic<String, LaneDirectionOfTravelNotification> inputLaneDirectionOfTravel = driver.createInputTopic(
                laneDirectionOfTravelTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.LaneDirectionOfTravelAssessmentNotification().serializer());

            inputLaneDirectionOfTravel.pipeInput("12109", ldotNotification);

            TestInputTopic<String, IntersectionReferenceAlignmentNotification> inputIntersectionReferenceAlignment = driver.createInputTopic(
                intersectionReferenceAlignmentTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.IntersectionReferenceAlignmentNotification().serializer());

            inputIntersectionReferenceAlignment.pipeInput("12109", iraNotification);

            TestInputTopic<String, SignalGroupAlignmentNotification> inputSignalGroupAlignment = driver.createInputTopic(
                signalGroupAlignmentTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.SignalGroupAlignmentNotification().serializer());

            inputSignalGroupAlignment.pipeInput("12109", sgaNotification);

            TestInputTopic<String, SignalStateConflictNotification> inputSignalStateConflictNotification = driver.createInputTopic(
                signalStateConflictTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.SignalStateConflictNotification().serializer());

            inputSignalStateConflictNotification.pipeInput("12109", sscNotification);

            TestInputTopic<String, TimeChangeDetailsNotification> inputTimeChangeDetails = driver.createInputTopic(
                spatTimeChangeDetailsTopicName, 
                Serdes.String().serializer(), 
                JsonSerdes.TimeChangeDetailsNotification().serializer());

            inputTimeChangeDetails.pipeInput("12109", tcdNotification);

            TestInputTopic<String, Notification> inputTimestampDeltaNotification = driver.createInputTopic(
                    timestampDeltaNotificationTopicName,
                    Serdes.String().serializer(),
                    JsonSerdes.Notification().serializer()
            );
            inputTimestampDeltaNotification.pipeInput("12109", mapTimestampDeltaNotification);
            inputTimestampDeltaNotification.pipeInput("12109", spatTimestampDeltaNotification);
            

            TestOutputTopic<String, Notification> outputNotificationTopic = driver.createOutputTopic(
                notificationTopicName, 
                Serdes.String().deserializer(), 
                JsonSerdes.Notification().deserializer());
            

            List<KeyValue<String, Notification>> notificationResults = outputNotificationTopic.readKeyValuesToList();

            
            
            assertEquals(8, notificationResults.size());
 
            for(KeyValue<String, Notification> notificationKeyValue: notificationResults){
                assertEquals("12109", notificationKeyValue.key);
                Notification notification = notificationKeyValue.value;
                String type = notification.getNotificationType();
                if(type.equals("ConnectionOfTravelNotification")){
                    assertEquals((ConnectionOfTravelNotification) notification, cotNotification);
                }
                else if(type.equals("IntersectionReferenceAlignmentNotification")){
                    assertEquals((IntersectionReferenceAlignmentNotification) notification, iraNotification);
                }
                else if(type.equals("LaneDirectionOfTravelAssessmentNotification")){
                    assertEquals((LaneDirectionOfTravelNotification) notification, ldotNotification);
                }
                else if(type.equals("SignalGroupAlignmentNotification")){
                    assertEquals((SignalGroupAlignmentNotification) notification, sgaNotification);
                }
                else if(type.equals("SignalStateConflictNotification")){
                    assertEquals((SignalStateConflictNotification) notification, sscNotification);
                }
                else if(type.equals("TimeChangeDetailsNotification")){
                    assertEquals((TimeChangeDetailsNotification) notification, tcdNotification);          
                }
                else if (type.equals("MapTimestampDeltaNotification")) {
                    assertEquals(notification, mapTimestampDeltaNotification);
                } else if (type.equals("SpatTimestampDeltaNotification")) {
                    assertEquals(notification, spatTimestampDeltaNotification);
                }
                else{
                    assertEquals(1,0);
                }
            }            
        }
        assertEquals(0,0);
    }
}