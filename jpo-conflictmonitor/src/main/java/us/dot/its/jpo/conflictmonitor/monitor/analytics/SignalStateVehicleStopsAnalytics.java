package us.dot.its.jpo.conflictmonitor.monitor.analytics;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsConstants.*;

import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_stops.SignalStateVehicleStopsParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneConnection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.SignalStateStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;



@Component(DEFAULT_SIGNAL_STATE_VEHICLE_STOPS_ALGORITHM)
public class SignalStateVehicleStopsAnalytics implements SignalStateVehicleStopsAlgorithm{
   

    @Override
    public SignalStateStopEvent getSignalStateStopEvent(SignalStateVehicleStopsParameters parameters, VehiclePath path, SpatAggregator spats){

        Lane ingressLane = path.getIngressLane();
        Lane egressLane = path.getEgressLane();

        
        if(ingressLane == null || egressLane == null){
            // Don't generate an event if the vehicle didn't go through the intersection
            return null;
        }
        
        OdeBsmData bsm = path.getIngressBsm();


        J2735Bsm bsmData = (J2735Bsm)bsm.getPayload().getData();
        if(bsmData != null && bsmData.getCoreData().getSpeed().doubleValue() > parameters.getStopSpeedThreshold()){
            // Don't generate an Event if the vehicle is moving.
            System.out.println("Vehicle did not stop at light");
            return null;
        }

        

        long bsmTime = BsmTimestampExtractor.getBsmTimestamp(bsm);
        ProcessedSpat matchingSpat = spats.getSpatAtTime(bsmTime);

        if(matchingSpat == null || Math.abs(spats.getSpatTimeDelta(matchingSpat, bsmTime)) > parameters.getSpatBsmMatchWindowMillis()){
            System.out.println("Spat and BSM Timestamps do not align");
            System.out.println("Spat Time: " + SpatTimestampExtractor.getSpatTimestamp(matchingSpat) + " BSM Time: " + bsmTime);
            // Don't generate event if the spat time delta is greater than configurable threshold
            return null;
        }

        LaneConnection connection = path.getIntersection().getLaneConnection(ingressLane, egressLane);
        
        J2735MovementPhaseState signalState = getSignalGroupState(matchingSpat, connection.getSignalGroup());

        if(signalState == null){
            System.out.println("There is no corresponding Signal Group for the provided lane connection");
            return null;
        }

        

        SignalStateStopEvent event = new SignalStateStopEvent();
        
        
        return event;
    }

    public J2735MovementPhaseState getSignalGroupState(ProcessedSpat spat, int signalGroup){
        long spatTime = SpatTimestampExtractor.getSpatTimestamp(spat);
        for(MovementState state: spat.getStates()){
            if(state.getSignalGroup() == signalGroup && state.getStateTimeSpeed().size() > 0){
                return state.getStateTimeSpeed().get(0).getEventState();
            }
        }
        return null;
    }
}
