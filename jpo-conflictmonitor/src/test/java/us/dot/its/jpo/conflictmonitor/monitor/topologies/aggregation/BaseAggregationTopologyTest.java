package us.dot.its.jpo.conflictmonitor.monitor.topologies.aggregation;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.AggregationParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.aggregation.EventTopicMap;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.Event;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.EventAggregation;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Base class for testing Event Aggregation topologies in isolation.
 * @param <TEventKey> Key Type
 * @param <TEvent> Event Type
 * @param <TAggKey> Aggregation Key Type
 * @param <TAggEvent> Aggregation Event Type
 * @param <TTopo> The topology to test
 */
public abstract class BaseAggregationTopologyTest<
        TEventKey,
        TEvent extends Event,
        TAggKey,
        TAggEvent extends EventAggregation<TEvent>,
        TTopo extends BaseAggregationTopology<TAggKey, TEvent, TAggEvent>> {

    protected final String inputTopicName = "inputEventTopic";
    protected final int numberOfEvents = 9;
    protected final String rsuId = "127.0.0.1";
    protected final int intersectionId = 11111;
    protected final int region = 10;
    protected final int intervalSeconds = 10;
    protected final int checkIntervalMs = 1000;

    // Start clock at top of hour
    protected final Instant initialWallClock = Instant.parse("2024-11-25T10:00:00Z");

    abstract String outputTopicName();
    abstract Serde<TEventKey> eventKeySerde();
    abstract Serde<TEvent> eventSerde();
    abstract Serde<TAggKey> aggKeySerde();
    abstract Serde<TAggEvent> aggEventSerde();
    abstract TEventKey createKey();
    abstract TEvent createEvent();
    abstract TTopo createTopology();
    abstract KStream<TAggKey, TEvent> selectAggKey(KStream<TEventKey, TEvent> instream);

    protected List<KeyValue<TAggKey, TAggEvent>> runTestTopology() {
        var streamsConfig = new Properties();
        Topology topology = createTestTopology(createTopology());

        List<KeyValue<TAggKey, TAggEvent>> resultList;

        try (var driver = new TopologyTestDriver(topology, streamsConfig, initialWallClock);
             var inputKeySerde = eventKeySerde();
             var inputEventSerde = eventSerde();
             var outputKeySerde = aggKeySerde();
             var outputAggEventSerde = aggEventSerde()) {

            var inputEventTopic = driver.createInputTopic(inputTopicName,
                    inputKeySerde.serializer(), inputEventSerde.serializer());

            var outputAggTopic = driver.createOutputTopic(outputTopicName(),
                    outputKeySerde.deserializer(), outputAggEventSerde.deserializer());

            // Send a bunch of events
            var sendInstant = initialWallClock;
            final var inputKey = createKey();
            for (int i = 1; i <= numberOfEvents; i++) {
                inputEventTopic.pipeInput(inputKey, createEvent(), sendInstant);
                sendInstant = sendInstant.plusMillis(checkIntervalMs);
                // Advance the wall clock
                driver.advanceWallClockTime(Duration.ofMillis(checkIntervalMs));
            }

            // Run out the wall clock
            driver.advanceWallClockTime(Duration.ofMillis(checkIntervalMs));
            driver.advanceWallClockTime(Duration.ofMillis(checkIntervalMs));

            // There should be one aggregated event produced
            resultList = outputAggTopic.readKeyValuesToList();
        }
        return resultList;
    }


    // Creates a test topology, to plug the aggregation topology into, that just feeds events to the aggregation topology
    protected Topology createTestTopology(BaseAggregationTopology<TAggKey, TEvent, TAggEvent> aggTopology) {
        var parameters = createAggParameters();

        aggTopology.setParameters(parameters);

        var builder = new StreamsBuilder();
        KStream<TEventKey, TEvent> eventStream = builder.stream(
                inputTopicName,
                Consumed.with(
                        eventKeySerde(),
                        eventSerde()
                )
        );

        KStream<TAggKey, TEvent> rekeyedEventStream = selectAggKey(eventStream);

        aggTopology.buildTopology(builder, rekeyedEventStream);
        return builder.build();
    }



    protected AggregationParameters createAggParameters() {
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

    protected EventTopicMap createEventTopicMap() {
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
}
