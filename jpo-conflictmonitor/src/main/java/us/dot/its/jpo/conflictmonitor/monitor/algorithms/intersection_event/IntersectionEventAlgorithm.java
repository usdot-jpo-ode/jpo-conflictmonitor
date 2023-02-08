package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.Algorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ExecutableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;


public interface IntersectionEventAlgorithm extends ExecutableAlgorithm { 
    
    LaneDirectionOfTravelAlgorithm getLaneDirectionOfTravelAlgorithm();
    LaneDirectionOfTravelParameters getLaneDirectionOfTravelParams();
    ConnectionOfTravelAlgorithm getConnectionOfTravelAlgorithm();
    ConnectionOfTravelParameters getConnectionOfTravelParams();
    SignalStateVehicleCrossesAlgorithm getSignalStateVehicleCrossesAlgorithm();
    SignalStateVehicleCrossesParameters getSignalStateVehicleCrossesParameters();
    SignalStateVehicleStopsAlgorithm getSignalStateVehicleStopsAlgorithm();
    SignalStateVehicleStopsParameters getSignalStateVehicleStopsParameters();


    void setLaneDirectionOfTravelAlgorithm(LaneDirectionOfTravelAlgorithm laneAlgorithm);
    void setLaneDirectionOfTravelParams(LaneDirectionOfTravelParameters laneParams);
    void setConnectionOfTravelAlgorithm(ConnectionOfTravelAlgorithm connTravelAlgorithm);
    void setConnectionOfTravelParams(ConnectionOfTravelParameters connTravelParams);
    void setSignalStateVehicleCrossesAlgorithm(SignalStateVehicleCrossesAlgorithm crossesAlgorithm);
    void setSignalStateVehicleCrossesParameters(SignalStateVehicleCrossesParameters crossesParams);
    void setSignalStateVehicleStopsAlgorithm(SignalStateVehicleStopsAlgorithm stopsAlgorithm);
    void setSignalStateVehicleStopsParameters(SignalStateVehicleStopsParameters stopsParams);
}
