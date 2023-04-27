package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
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
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmEvent;
import us.dot.its.jpo.conflictmonitor.monitor.serialization.JsonSerdes;
import us.dot.its.jpo.conflictmonitor.testutils.BsmTestUtils;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

import java.time.Instant;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@RunWith(MockitoJUnitRunner.class)
public class IntersectionEventTopologyTest {

    final String bsmEventTopic = "topic.CMBsmEvents";
    final String laneDirectionOfTravelTopic = "topic.CmLaneDirectionOfTravelEvent";
    final String connectionOfTravelTopic = "topic.CmConnectionOfTravelEvent";
    final String signalStateTopic = "topic.CmSignalStateEvent";
    final String signalStopTopic = "topic.CmSignalStopEvent";

    Properties streamsProperties = new Properties();
    @Mock ReadOnlyWindowStore<String, OdeBsmData> bsmWindowStore;
    @Mock ReadOnlyWindowStore<String, ProcessedSpat> spatWindowStore;
    @Mock ReadOnlyKeyValueStore<String, ProcessedMap> mapWindowStore;
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
    final String bsmId = "A0A0A0";

    @Test
    public void testIntersectionEventTopology() {
        var conflictMonitorProperties = new ConflictMonitorProperties();
        conflictMonitorProperties.setKafkaTopicCmBsmEvent(bsmEventTopic);
        conflictMonitorProperties.setKafkaTopicCmLaneDirectionOfTravelEvent(laneDirectionOfTravelTopic);
        conflictMonitorProperties.setKafkaTopicCmConnectionOfTravelEvent(connectionOfTravelTopic);
        conflictMonitorProperties.setKafkaTopicCmSignalStateEvent(signalStateTopic);
        conflictMonitorProperties.setKafakTopicCmVehicleStopEvent(signalStopTopic);
        var intersectionEventTopology = new IntersectionEventTopology();
        intersectionEventTopology.setConflictMonitorProperties(conflictMonitorProperties);
        intersectionEventTopology.setStreamsProperties(streamsProperties);
        intersectionEventTopology.setBsmWindowStore(bsmWindowStore);
        intersectionEventTopology.setSpatWindowStore(spatWindowStore);
        intersectionEventTopology.setMapStore(mapWindowStore);
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
                    Serdes.String().serializer(),
                    JsonSerdes.BsmEvent().serializer());
            var connectionOfTravelOutputTopic = driver.createOutputTopic(
                    connectionOfTravelTopic,
                    Serdes.String().deserializer(),
                    JsonSerdes.ConnectionOfTravelEvent().deserializer());
            BsmEvent event = new BsmEvent();
            var startBsm = BsmTestUtils.bsmAtInstant(Instant.ofEpochMilli(startMillis), bsmId);
            var endBsm = BsmTestUtils.bsmAtInstant(Instant.ofEpochMilli(endMillis), bsmId);
            event.setStartingBsm(startBsm);
            event.setEndingBsm(endBsm);
            event.setStartingBsmTimestamp(startMillis);
            event.setEndingBsmTimestamp(endMillis);
            bsmInputTopic.pipeInput("1", event);
        }
    }
}
