package us.dot.its.jpo.conflictmonitor.monitor.algorithms.intersection_event;

import us.dot.its.jpo.conflictmonitor.ConflictMonitorProperties;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.ExecutableAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageParameters;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopParameters;


public interface IntersectionEventAlgorithm extends ExecutableAlgorithm { 
    
    ConflictMonitorProperties getConflictMonitorProperties();
    LaneDirectionOfTravelAlgorithm getLaneDirectionOfTravelAlgorithm();
    LaneDirectionOfTravelParameters getLaneDirectionOfTravelParams();
    ConnectionOfTravelAlgorithm getConnectionOfTravelAlgorithm();
    ConnectionOfTravelParameters getConnectionOfTravelParams();
    StopLinePassageAlgorithm getSignalStateVehicleCrossesAlgorithm();
    StopLinePassageParameters getStopLinePassageParameters();
    StopLineStopAlgorithm getSignalStateVehicleStopsAlgorithm();
    StopLineStopParameters getStopLineStopParameters();

    void setConflictMonitorProperties(ConflictMonitorProperties conflictMonitorProps);
    void setLaneDirectionOfTravelAlgorithm(LaneDirectionOfTravelAlgorithm laneAlgorithm);
    void setLaneDirectionOfTravelParams(LaneDirectionOfTravelParameters laneParams);
    void setConnectionOfTravelAlgorithm(ConnectionOfTravelAlgorithm connTravelAlgorithm);
    void setConnectionOfTravelParams(ConnectionOfTravelParameters connTravelParams);
    void setSignalStateVehicleCrossesAlgorithm(StopLinePassageAlgorithm crossesAlgorithm);
    void setStopLinePassageParameters(StopLinePassageParameters crossesParams);
    void setSignalStateVehicleStopsAlgorithm(StopLineStopAlgorithm stopsAlgorithm);
    void setStopLineStopParameters(StopLineStopParameters stopsParams);
}
