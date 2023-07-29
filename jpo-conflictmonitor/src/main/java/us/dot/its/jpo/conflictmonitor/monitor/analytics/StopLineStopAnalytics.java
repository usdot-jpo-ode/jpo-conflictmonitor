package us.dot.its.jpo.conflictmonitor.monitor.analytics;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopConstants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneConnection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLineStopEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;



@Component(DEFAULT_SIGNAL_STATE_VEHICLE_STOPS_ALGORITHM)
public class StopLineStopAnalytics implements StopLineStopAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(StopLineStopAnalytics.class);
   

    @Override
    public StopLineStopEvent getStopLineStopEvent(StopLineStopParameters parameters, VehiclePath path, SpatAggregator spats){

        Lane ingressLane = path.getIngressLane();
        Lane egressLane = path.getEgressLane();

        
        if(ingressLane == null){
            logger.info("No ingress lane found for path {}, can't generate StopLineStop event", path);
            return null;
        }
        
        OdeBsmData bsm = path.getIngressBsm();


        J2735Bsm bsmData = (J2735Bsm)bsm.getPayload().getData();
        if(bsmData != null && bsmData.getCoreData().getSpeed().doubleValue() > parameters.getStopSpeedThreshold()){
            // Don't generate an Event if the vehicle is moving.
            return null;
        }

        

        long bsmTime = BsmTimestampExtractor.getBsmTimestamp(bsm);
        ProcessedSpat matchingSpat = spats.getSpatAtTime(bsmTime);

        if(matchingSpat == null || Math.abs(spats.getSpatTimeDelta(matchingSpat, bsmTime)) > parameters.getSpatBsmMatchWindowMillis()){
            // Don't generate event if the spat time delta is greater than configurable threshold
            return null;
        }

        LaneConnection connection = path.getIntersection().getLaneConnection(ingressLane, egressLane);
        
        J2735MovementPhaseState signalState = getSignalGroupState(matchingSpat, connection.getSignalGroup());

        if(signalState == null){
            // Don't generate event if no corresponding signal group can be found for the lane connection
            return null;
        }

        

        StopLineStopEvent event = new StopLineStopEvent();
        event.setIntersectionID(path.getIntersection().getIntersectionId());
        event.setRoadRegulatorID(path.getIntersection().getRoadRegulatorId());
        event.setIngressLane(ingressLane.getId());
        if (egressLane != null) {
            event.setEgressLane(egressLane.getId());
        }
        event.setHeading(BsmUtils.getHeading(bsm).orElse(0d));
        ;
        
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
