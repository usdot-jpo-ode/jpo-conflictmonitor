package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.EventTopicMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.minimum_data.SpatMinimumDataEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.geojsonconverter.partitioner.IntersectionIdPartitioner;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@Slf4j
public class SpatMinimumDataAggregationTopologyTest {

    final String inputTopicName = "inputEventTopic";


    @Test
    public void testSpatMinimumDataAggregationTopology() {
        var streamsConfig = new Properties();
        Topology topology = createTestTopology();

        // Start clock at top of hour
        final var initialWallClock = Instant.parse("2024-11-25T10:00:00Z");

        try (var driver = new TopologyTestDriver(topology, streamsConfig, initialWallClock);
             var inputKeySerde = us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
             var inputEventSerde = JsonSerdes.SpatMinimumDataEvent();
             var outputKeySerde = us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey();
             var outputAggEventSerde = JsonSerdes.SpatMinimumDataEventAggregation()) {

            var inputEventTopic = driver.createInputTopic(inputTopicName,
                    inputKeySerde.serializer(), inputEventSerde.serializer());

            var outputAggTopic = driver.createOutputTopic("topic.CmSpatMinimumDataEventAggregation",
                    outputKeySerde.deserializer(), outputAggEventSerde.deserializer());

            // Send a bunch of events 1 second apart
            var sendInstant = initialWallClock;
            for (int i = 1; i <= 9; i++) {
                inputEventTopic.pipeInput(createKey(), createEvent(), sendInstant);
                sendInstant = sendInstant.plusMillis(checkIntervalMs);
                // Advance the wall clock
                driver.advanceWallClockTime(Duration.ofMillis(checkIntervalMs));
            }

            // Run out the wall clock
            driver.advanceWallClockTime(Duration.ofMillis(checkIntervalMs));
            driver.advanceWallClockTime(Duration.ofMillis(checkIntervalMs));

            var resultList = outputAggTopic.readKeyValuesToList();
            assertThat("Should have produced 1 aggregated event", resultList, hasSize(1));

        }
    }

    // Creates a test topology that just feeds events to the aggregation topology
    protected Topology createTestTopology() {
        var parameters = createAggParameters();
        var aggTopology = new SpatMinimumDataAggregationTopology();
        aggTopology.setParameters(parameters);

        var builder = new StreamsBuilder();
        KStream<RsuIntersectionKey, SpatMinimumDataEvent> eventStream = builder.stream(
                inputTopicName,
                  Consumed.with(
                          us.dot.its.jpo.geojsonconverter.serialization.JsonSerdes.RsuIntersectionKey(),
                          JsonSerdes.SpatMinimumDataEvent()
                  )
        );

        aggTopology.buildTopology(builder, eventStream);
        return builder.build();
    }

    final int intervalSeconds = 10;
    final int checkIntervalMs = 1000;

    AggregationParameters createAggParameters() {
        var aggParams = new AggregationParameters();
        aggParams.setDebug(true);
        aggParams.setInterval(intervalSeconds);
        aggParams.setIntervalUnits(ChronoUnit.SECONDS);
        aggParams.setCheckIntervalMs(checkIntervalMs);
        aggParams.setGracePeriodMs(0);
        aggParams.setEventTopicMap(createEventTopicMap());
        // eventAlgorithm map not needed for this test
        return aggParams;
    }

    EventTopicMap createEventTopicMap() {
        var map = Map.of(
                "SpatMinimumDataAggregation", "topic.CmSpatMinimumDataEventAggregation",
                "MapMinimumDataAggregation", "topic.CmMapMinimumDataEventAggregation",
                "IntersectionReferenceAlignmentAggregation", "topic.CmIntersectionReferenceAlignmentEventAggregation",
                "SignalGroupAlignmentAggregation", "topic.CmSignalGroupAlignmentEventAggregation",
                "SignalStateConflictAggregation", "topic.CmSignalStateConflictEventAggregation",
                "TimeChangeDetailsAggregation", "topic.CmSpatTimeChangeDetailsEventAggregation",
                "EventStateProgressionAggregation", "topic.CmEventStateProgressionEventAggregation"
        );
        var eventTopicMap = new EventTopicMap();
        eventTopicMap.putAll(map);
        return eventTopicMap;
    }

    final String rsuId = "127.0.0.1";
    final int intersectionId = 11111;
    final int region = 10;

    RsuIntersectionKey createKey() {
        return new RsuIntersectionKey(rsuId, intersectionId, region);
    }

    final AtomicInteger elementNum = new AtomicInteger(1);

    SpatMinimumDataEvent createEvent() {
        var event = new SpatMinimumDataEvent();
        event.setSource(rsuId);
        event.setIntersectionID(intersectionId);
        event.setRoadRegulatorID(region);
        int element = elementNum.getAndIncrement();
        event.setMissingDataElements(List.of(Integer.toString(element)));
        return event;
    }
}
