package us.dot.its.jpo.conflictmonitor.monitor.analytics;

import org.springframework.stereotype.Component;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelConstants.*;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.connection_of_travel.ConnectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneConnection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.ConnectionOfTravelEvent;

@Component(DEFAULT_CONNECTION_OF_TRAVEL_ALGORITHM)
public class ConnectionOfTravelAnalytics implements ConnectionOfTravelAlgorithm{
    

   
    @Override
    public ConnectionOfTravelEvent getConnectionOfTravelEvent(ConnectionOfTravelParameters parameters, VehiclePath path){

        Lane ingressLane = path.getIngressLane();
        Lane egressLane = path.getEgressLane();

        if(ingressLane == null || egressLane == null){
            return null;
        }        

        ConnectionOfTravelEvent event = new ConnectionOfTravelEvent();
        //event.setTimestamp(BsmTimestampExtractor.getBsmTimestamp(path.getIngressBsm()));
        event.setRoadRegulatorId(path.getIntersection().getRoadRegulatorId());
        event.setIntersectionId(path.getIntersection().getIntersectionId());
        event.setIngressLaneId(ingressLane.getId());
        event.setEgressLaneId(egressLane.getId());

        LaneConnection connection = path.getIntersection().getLaneConnection(ingressLane, egressLane);
        if(connection != null){
            event.setConnectionId(connection.getConnectionId());
        }else{
            event.setConnectionId(-1);
        }
        
        

        return event;
    }

}
