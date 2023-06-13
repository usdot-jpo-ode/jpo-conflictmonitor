package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.state.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmIntersectionKey;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils;
import us.dot.its.jpo.conflictmonitor.testutils.SpatTestUtils;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

import java.time.Instant;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class IntersectionEventTopologyTest {

    final String bsmEventTopic = "topic.CMBsmEvents";
    final String laneDirectionOfTravelTopic = "topic.CmLaneDirectionOfTravelEvent";
    final String connectionOfTravelTopic = "topic.CmConnectionOfTravelEvent";
    final String signalStateTopic = "topic.CmSignalStateEvent";
    final String signalStopTopic = "topic.CmSignalStopEvent";
    final String bsmStoreName = "BsmWindowStore";
    final String spatStoreName = "SpatWindowStore";
    final String mapStoreName = "ProcessedMapWindowStore";

    Properties streamsProperties = new Properties();
    @Mock ReadOnlyWindowStore<String, OdeBsmData> bsmWindowStore;
    @Mock KeyValueIterator<Windowed<String>, OdeBsmData> bsmWindowStoreIterator;
    @Mock ReadOnlyWindowStore<String, ProcessedSpat> spatWindowStore;
    @Mock KeyValueIterator<Windowed<String>, ProcessedSpat> spatWindowStoreIterator;
    @Mock ReadOnlyKeyValueStore<String, ProcessedMap<LineString>> mapStore;

    @Mock LaneDirectionOfTravelAlgorithm laneDirectionOfTravelAlgorithm;
    LaneDirectionOfTravelParameters laneDirectionOfTravelParameters = new LaneDirectionOfTravelParameters();
    @Mock ConnectionOfTravelAlgorithm connectionOfTravelAlgorithm;
    ConnectionOfTravelParameters connectionOfTravelParameters = new ConnectionOfTravelParameters();
    @Mock SignalStateVehicleCrossesAlgorithm signalStateVehicleCrossesAlgorithm;
    SignalStateVehicleCrossesParameters signalStateVehicleCrossesParameters = new SignalStateVehicleCrossesParameters();
    @Mock SignalStateVehicleStopsAlgorithm signalStateVehicleStopsAlgorithm;
    SignalStateVehicleStopsParameters signalStateVehicleStopsParameters = new SignalStateVehicleStopsParameters();

    final long startMillis = 1682615309868L;
    final long endMillis = 1682615347488L;
    final BsmIntersectionKey bsmId = new BsmIntersectionKey("127.0.0.1", "A0A0A0");

    @Test
    public void testIntersectionEventTopology() {

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

//        when(bsmWindowStoreIterator.hasNext()).thenReturn(true, true, false);
//        when(bsmWindowStoreIterator.next()).thenReturn(kvStartBsm, kvEndBsm);
//        when(bsmWindowStore.fetchAll(any(Instant.class), any(Instant.class))).thenReturn(bsmWindowStoreIterator);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);

//        when(spatWindowStoreIterator.hasNext()).thenReturn(true, false);
//        when(spatWindowStoreIterator.next()).thenReturn(kvSpat);
//        when(spatWindowStore.fetchAll(any(Instant.class), any(Instant.class))).thenReturn(spatWindowStoreIterator);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);

        final var map = new ProcessedMap<LineString>();
//        when(mapStore.get(anyString())).thenReturn(map);
        intersectionEventTopology.setMapStore(mapStore);

        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setSignalStateVehicleCrossesParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
        intersectionEventTopology.setSignalStateVehicleStopsParameters(signalStateVehicleStopsParameters);
        Topology topology = intersectionEventTopology.buildTopology();



        try (TopologyTestDriver driver = new TopologyTestDriver(topology, streamsProperties)) {


            var bsmInputTopic = driver.createInputTopic(
                    bsmEventTopic,
                    JsonSerdes.BsmIntersectionKey().serializer(),
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
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_BsmWindowStore() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_SpatWindowStore() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_MapStore() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_LaneDirectionOfTravelAlgorithm() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_LaneDirectionOfTravelParams() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_ConnectionOfTravelAlgorithm() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_ConnectionOfTravelParams() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_SignalStateVehicleCrossesAlgorithm() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
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
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
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
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setSignalStateVehicleCrossesParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_SignalStateVehicleStopsParameters() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setSignalStateVehicleCrossesParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
        intersectionEventTopology.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidate_AlreadyRunning() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setSignalStateVehicleCrossesParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
        intersectionEventTopology.setSignalStateVehicleStopsParameters(signalStateVehicleStopsParameters);

        KafkaStreams streams = mock(KafkaStreams.class);
        when(streams.state()).thenReturn(KafkaStreams.State.RUNNING);
        intersectionEventTopology.setStreams(streams);

        intersectionEventTopology.validate();
    }

    @Test
    public void testValidate() {
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapStore);
        intersectionEventTopology.setLaneDirectionOfTravelAlgorithm(laneDirectionOfTravelAlgorithm);
        intersectionEventTopology.setLaneDirectionOfTravelParams(laneDirectionOfTravelParameters);
        intersectionEventTopology.setConnectionOfTravelAlgorithm(connectionOfTravelAlgorithm);
        intersectionEventTopology.setConnectionOfTravelParams(connectionOfTravelParameters);
        intersectionEventTopology.setSignalStateVehicleCrossesAlgorithm(signalStateVehicleCrossesAlgorithm);
        intersectionEventTopology.setSignalStateVehicleCrossesParameters(signalStateVehicleCrossesParameters);
        intersectionEventTopology.setSignalStateVehicleStopsAlgorithm(signalStateVehicleStopsAlgorithm);
        intersectionEventTopology.setSignalStateVehicleStopsParameters(signalStateVehicleStopsParameters);
        intersectionEventTopology.validate();

        assertThat(intersectionEventTopology.getStreamsProperties(), notNullValue());
        assertThat(intersectionEventTopology.getBsmWindowStore(), notNullValue());
        assertThat(intersectionEventTopology.getSpatWindowStore(), notNullValue());
        assertThat(intersectionEventTopology.getMapStore(), notNullValue());
        assertThat(intersectionEventTopology.getLaneDirectionOfTravelAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getLaneDirectionOfTravelParams(), notNullValue());
        assertThat(intersectionEventTopology.getConnectionOfTravelAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getConnectionOfTravelParams(), notNullValue());
        assertThat(intersectionEventTopology.getSignalStateVehicleCrossesAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getSignalStateVehicleCrossesParameters(), notNullValue());
        assertThat(intersectionEventTopology.getSignalStateVehicleStopsAlgorithm(), notNullValue());
        assertThat(intersectionEventTopology.getSignalStateVehicleStopsParameters(), notNullValue());
    }

}
