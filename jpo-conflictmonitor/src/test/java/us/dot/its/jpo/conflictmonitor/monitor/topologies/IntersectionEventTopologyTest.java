package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.message_ingest.MessageIngestStreamsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmRsuIdKey;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils;
import us.dot.its.jpo.conflictmonitor.testutils.SpatTestUtils;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuIntersectionKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

import java.time.Instant;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class IntersectionEventTopologyTest {

    final String bsmEventTopic = "topic.CMBsmEvents";
    final String laneDirectionOfTravelTopic = "topic.CmLaneDirectionOfTravelEvent";
    final String connectionOfTravelTopic = "topic.CmConnectionOfTravelEvent";
    final String signalStateTopic = "topic.CmSignalStateEvent";
    final String signalStopTopic = "topic.CmSignalStopEvent";


    Properties streamsProperties = new Properties();
    @Mock ReadOnlyWindowStore<BsmRsuIdKey, OdeBsmData> bsmWindowStore;
    @Mock KeyValueIterator<Windowed<String>, OdeBsmData> bsmWindowStoreIterator;
    @Mock ReadOnlyWindowStore<RsuIntersectionKey, ProcessedSpat> spatWindowStore;
    @Mock KeyValueIterator<Windowed<String>, ProcessedSpat> spatWindowStoreIterator;
    @Mock ReadOnlyKeyValueStore<RsuIntersectionKey, ProcessedMap<LineString>> mapStore;

    @Mock
    MessageIngestStreamsAlgorithm messageIngestAlgorithm;

    @Mock LaneDirectionOfTravelAlgorithm laneDirectionOfTravelAlgorithm;
    LaneDirectionOfTravelParameters laneDirectionOfTravelParameters = new LaneDirectionOfTravelParameters();
    @Mock ConnectionOfTravelAlgorithm connectionOfTravelAlgorithm;
    ConnectionOfTravelParameters connectionOfTravelParameters = new ConnectionOfTravelParameters();
    @Mock
    StopLinePassageAlgorithm signalStateVehicleCrossesAlgorithm;
    StopLinePassageParameters signalStateVehicleCrossesParameters = new StopLinePassageParameters();
    @Mock
    StopLineStopAlgorithm signalStateVehicleStopsAlgorithm;
    StopLineStopParameters signalStateVehicleStopsParameters = new StopLineStopParameters();

    final long startMillis = 1682615309868L;
    final long endMillis = 1682615347488L;
    final BsmRsuIdKey bsmId = new BsmRsuIdKey("127.0.0.1", "A0A0A0");

    @Test
    public void testIntersectionEventTopology() {

        // Return builder inputs unchanged for embedded topologies
        //when(messageIngestAlgorithm.buildTopology(any())).thenAnswer(i -> i.getArguments()[0]);

        final var startBsm = BsmTestUtils.bsmAtInstant(Instant.ofEpochMilli(startMillis), bsmId.getBsmId());
        final var endBsm = BsmTestUtils.bsmAtInstant(Instant.ofEpochMilli(endMillis), bsmId.getBsmId());
        final KeyValue<Windowed<String>, OdeBsmData> kvStartBsm = new KeyValue<>(new Windowed<>(bsmId.getBsmId(), new TimeWindow(startMillis, startMillis + 30000)), startBsm);
        final KeyValue<Windowed<String>, OdeBsmData> kvEndBsm = new KeyValue<>(new Windowed<>(bsmId.getBsmId(), new TimeWindow(startMillis, startMillis + 30000)), endBsm);

        final int intersectionId = 1;
        final ProcessedSpat spat = SpatTestUtils.validSpat(intersectionId);
        final KeyValue<Windowed<String>, ProcessedSpat> kvSpat = new KeyValue<>(new Windowed<>("1", new TimeWindow(startMillis, startMillis + 30000)), spat);

        var conflictMonitorProperties = new ConflictMonitorProperties();
        conflictMonitorProperties.setKafkaTopicCmBsmEvent(bsmEventTopic);
        conflictMonitorProperties.setKafkaTopicCmLaneDirectionOfTravelEvent(laneDirectionOfTravelTopic);
        conflictMonitorProperties.setKafkaTopicCmConnectionOfTravelEvent(connectionOfTravelTopic);
        conflictMonitorProperties.setKafkaTopicCmSignalStateEvent(signalStateTopic);
        conflictMonitorProperties.setKafakTopicCmVehicleStopEvent(signalStopTopic);
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setConflictMonitorProperties(conflictMonitorProperties);
        intersectionEventTopology.setStreamsProperties(streamsProperties);

        final var map = new ProcessedMap<LineString>();

        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setStopLinePassageParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
        intersectionEventTopology.setStopLineStopParameters(signalStateVehicleStopsParameters);
        Topology topology = intersectionEventTopology.buildTopology();



        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsProperties)) {


            var bsmInputTopic = driver.createInputTopic(
                    bsmEventTopic,
                    JsonSerdes.BsmRsuIdKey().serializer(),
                    JsonSerdes.BsmEvent().serializer());
            var connectionOfTravelOutputTopic = driver.createOutputTopic(
                    connectionOfTravelTopic,
                    Serdes.String().deserializer(),
                    JsonSerdes.ConnectionOfTravelEvent().deserializer());
            BsmEvent event = new BsmEvent();

            event.setStartingBsm(startBsm);
            event.setEndingBsm(endBsm);
            event.setStartingBsmTimestamp(startMillis);
            event.setEndingBsmTimestamp(endMillis);

            bsmInputTopic.pipeInput(bsmId, event);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_StreamsProperties() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.validate();
    }


    @Test(expected = IllegalStateException.class)
    public void testValidate_LaneDirectionOfTravelAlgorithm() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_LaneDirectionOfTravelParams() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_ConnectionOfTravelAlgorithm() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_ConnectionOfTravelParams() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_SignalStateVehicleCrossesAlgorithm() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_SignalStateVehicleCrossesParams() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_SignalStateVehicleStopsAlgorithm() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);

        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setStopLinePassageParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_SignalStateVehicleStopsParameters() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);

        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setStopLinePassageParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_AlreadyRunning() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);

        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setStopLinePassageParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
        intersectionEventTopology.setStopLineStopParameters(signalStateVehicleStopsParameters);

        KafkaStreams streams = mock(KafkaStreams.class);
        when(streams.state()).thenReturn(KafkaStreams.State.RUNNING);
        intersectionEventTopology.setStreams(streams);

        intersectionEventTopology.validate();
    }

    @Test
    public void testValidate() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setMessageIngestAlgorithm(messageIngestAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setStopLinePassageParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
        intersectionEventTopology.setStopLineStopParameters(signalStateVehicleStopsParameters);

        intersectionEventTopology.validate();

        assertThat(intersectionEventTopology.getStreamsProperties(), notNullValue());
        assertThat(intersectionEventTopology.getMessageIngestAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getLaneDirectionOfTravelAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getLaneDirectionOfTravelParams(), notNullValue());
        assertThat(intersectionEventTopology.getConnectionOfTravelAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getConnectionOfTravelParams(), notNullValue());
        assertThat(intersectionEventTopology.getSignalStateVehicleCrossesAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getStopLinePassageParameters(), notNullValue());
        assertThat(intersectionEventTopology.getSignalStateVehicleStopsAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getStopLineStopParameters(), notNullValue());
    }

}
