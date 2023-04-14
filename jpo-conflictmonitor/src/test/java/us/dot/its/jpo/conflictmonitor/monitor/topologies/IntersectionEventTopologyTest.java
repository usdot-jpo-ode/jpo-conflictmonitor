package us.dot.its.jpo.conflictmonitor.monitor.topologies;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.ProcessedMap;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;

import java.util.Properties;


//@RunWith(MockitoJUnitRunner.class)
public class IntersectionEventTopologyTest {

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

    //@Test
    public void testStart() {
        var intersectionEventTopology = new IntersectionEventTopology();
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
        intersectionEventTopology.start();

    }
}
