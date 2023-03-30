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
        event.setRoadRegulatorID(path.getIntersection().getRoadRegulatorId());
        event.setIntersectionID(path.getIntersection().getIntersectionId());
        event.setIngressLaneID(ingressLane.getId());
        event.setEgressLaneID(egressLane.getId());
        event.setIntersectionID(path.getIntersection().getIntersectionId());
        event.setRoadRegulatorID(path.getIntersection().getRoadRegulatorId());


        LaneConnection connection = path.getIntersection().getLaneConnection(ingressLane, egressLane);
        if(connection != null){
            event.setConnectionID(connection.getConnectionId());
        }else{
            event.setConnectionID(-1);
        }
        
        

        return event;
    }

}
