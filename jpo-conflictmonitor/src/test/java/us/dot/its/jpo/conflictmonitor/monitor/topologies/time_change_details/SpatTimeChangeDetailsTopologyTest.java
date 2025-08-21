package us.dot.its.jpo.conflictmonitor.monitor.topologies.time_change_details;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.testng.collections.Lists;
import us.dot.its.jpo.conflictmonitor.monitor.models.notifications.TimeChangeDetailsNotification;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.time_change_details.spat.SpatTimeChangeDetailsParameters;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.spat.*;


import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;


public class SpatTimeChangeDetailsTopologyTest {
    String spatTimeChangeDetailsEventTopicName = "topic.CmSpatTimeChangeDetailsEvent";
    String spatTimeChangeDetailsNotificationTopicName = "topic.CmSpatTimeChangeDetailsNotification";
    @Test
    public void testTopology() {

        SpatTimeChangeDetailsTopology spatTopology = new SpatTimeChangeDetailsTopology();
        SpatTimeChangeDetailsParameters parameters = new SpatTimeChangeDetailsParameters();
        parameters.setSpatInputTopicName("topic.ProcessedSpat");
        parameters.setSpatTimeChangeDetailsStateStoreName("spat-time-change-detail-state-store");
        parameters.setDebug(false);
        parameters.setSpatTimeChangeDetailsTopicName(spatTimeChangeDetailsEventTopicName);
        parameters.setSpatTimeChangeDetailsNotificationTopicName(spatTimeChangeDetailsNotificationTopicName);
        parameters.setAggregateEvents(false);
        parameters.setJitterBufferSize(2);


        spatTopology.setParameters(parameters);

        Topology topology = spatTopology.buildTopology();

        final String rsuId = "127.0.0.1";
        final int region = 0;
        final int intersectionId = 12109;
        final int signalGroup = 5;
        final ProcessedMovementPhaseState phaseState = ProcessedMovementPhaseState.STOP_AND_REMAIN;
        final long timestamp1 = 1732215600000L; // Top of the hour
        final long timestamp2 = timestamp1 + 10L;
        final long timeMark1 = 5;
        final long timeMark2 = 3;
        // TimeMarks are 1/10 of a second relative to top of the hour, so millis = TimeMark * 100
        final long minEndTime1 = timestamp1 + timeMark1 * 100;
        final long minEndTime2 = timestamp1 + timeMark2 * 100;



        final var spat1 = spat(timestamp1, region, intersectionId, signalGroup, phaseState, minEndTime1);
        System.out.println(spat1);
        final var spat2 = spat(timestamp2, region, intersectionId, signalGroup, phaseState, minEndTime2);
        System.out.println(spat2);
        final var key = new RsuIntersectionKey(rsuId, intersectionId, region);
        System.out.println(key);


        try (TopologyTestDriver driver = new TopologyTestDriver(topology)) {

            var inputProcessedSpatTopic = driver.createInputTopic(
                    parameters.getSpatInputTopicName(),
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey().serializer(),
                    us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.ProcessedSpat().serializer()
            );

            var outputEventTopic = driver.createOutputTopic(
                    parameters.getSpatTimeChangeDetailsTopicName(),
                    JsonSerdes.RsuIntersectionSignalGroupKey().deserializer(),
                    JsonSerdes.TimeChangeDetailsEvent().deserializer()
            );

            TestOutputTopic<String, TimeChangeDetailsNotification> outputNotificationTopic = driver.createOutputTopic(
                spatTimeChangeDetailsNotificationTopicName, 
                Serdes.String().deserializer(),
                JsonSerdes.TimeChangeDetailsNotification().deserializer());
            
            inputProcessedSpatTopic.pipeInput(key, spat1, timestamp1);
            inputProcessedSpatTopic.pipeInput(key, spat2, timestamp2);

            final var eventResults = outputEventTopic.readKeyValuesToList();
            assertEquals(1, eventResults.size());
            final var eventResult = eventResults.getFirst();
            final var resultKey = eventResult.key;
            final var resultValue = eventResult.value;
            System.out.printf("result eventKey: %s, eventValue: %s%n", resultKey, resultValue);
            assertEquals(rsuId, resultKey.getRsuId());
            assertEquals(intersectionId, resultKey.getIntersectionId());
            assertEquals(region, resultKey.getRegion());
            assertEquals(signalGroup, resultKey.getSignalGroup());

            assertEquals(timeMark1, resultValue.getFirstConflictingTimemark());
            assertEquals(timeMark2, resultValue.getSecondConflictingTimemark());


            List<KeyValue<String, TimeChangeDetailsNotification>> notificationResults = outputNotificationTopic.readKeyValuesToList();
            assertEquals(1, notificationResults.size());

            KeyValue<String, TimeChangeDetailsNotification> notificationKeyValue = notificationResults.getFirst();

            assertNotNull(notificationKeyValue.key);


            TimeChangeDetailsNotification notification = notificationKeyValue.value;

            assertEquals("TimeChangeDetailsNotification", notification.getNotificationType());

            assertEquals("Time Change Details Notification, generated because corresponding time change details event was generated.", notification.getNotificationText());

            assertEquals("Time Change Details", notification.getNotificationHeading());

        }

    }

    private ProcessedSpat spat(
            final long utcTimestamp,
            final int region,
            final int intersectionId,
            final int signalGroup,
            final ProcessedMovementPhaseState phaseState,
            final long minEndTime) {

        final var spat1 = new ProcessedSpat();
        spat1.setUtcTimeStamp(zdt(utcTimestamp));
        spat1.setRegion(region);
        spat1.setIntersectionId(intersectionId);

        final var state1 = new ProcessedMovementState();
        state1.setSignalGroup(signalGroup);

        final var event1 = new ProcessedMovementEvent();
        event1.setEventState(phaseState);

        final var timing1 = new TimingChangeDetails();
        timing1.setMinEndTime(zdt(minEndTime));
        event1.setTiming(timing1);

        state1.setStateTimeSpeed(Lists.newArrayList(event1));

        spat1.setStates(Lists.newArrayList(state1));

        return spat1;
    }

    private ZonedDateTime zdt(final long timestamp) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
    }
}