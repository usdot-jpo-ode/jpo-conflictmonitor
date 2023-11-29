package us.dot.its.jpo.conflictmonitor.monitor.analytics;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageConstants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_passage.StopLinePassageParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneConnection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.Set;

@Component(DEFAULT_SIGNAL_STATE_VEHICLE_CROSSES_ALGORITHM)
public class StopLinePassageAnalytics implements StopLinePassageAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(StopLinePassageAnalytics.class);
  

    @Override
    public StopLinePassageEvent getStopLinePassageEvent(StopLinePassageParameters parameters, VehiclePath path, SpatAggregator spats){
        

        Lane ingressLane = path.getIngressLane();
        Lane egressLane = path.getEgressLane();

        if (ingressLane == null) {
            logger.info("No ingress lane found for path {}, can't generate StopLinePassage event", path);
            return null;
        }


        OdeBsmData bsm = path.getIngressBsm();


        long bsmTime = BsmTimestampExtractor.getBsmTimestamp(bsm);
        ProcessedSpat matchingSpat = spats.getSpatAtTime(bsmTime);

        if(matchingSpat == null || Math.abs(spats.getSpatTimeDelta(matchingSpat, bsmTime)) > parameters.getSpatBsmMatchWindowMillis()){
            // Don't generate event if the spat time delta is greater than configurable threshold
            return null;
        }

        int signalGroup = -1;
        int connectionId = -1;

        Set<Integer> signalGroups = path.getIntersection().getSignalGroupsForIngressLane(ingressLane);
        if(signalGroups.size() ==0){
            return null;
        }else if(signalGroups.size() ==1){
            signalGroup = signalGroups.iterator().next();
            if(egressLane != null){
                LaneConnection connection = path.getIntersection().getLaneConnection(ingressLane, egressLane);
                if(connection!= null){
                    connectionId = connection.getSignalGroup();
                }
            }
        }else if(signalGroups.size()>=2){
            if(egressLane != null){
                LaneConnection connection = path.getIntersection().getLaneConnection(ingressLane, egressLane);
                if(connection!= null){
                    signalGroup = connection.getSignalGroup();
                    connectionId = connection.getSignalGroup();
                }else{
                    Set<Integer> egressSignalGroups = path.getIntersection().getSignalGroupsForEgressLane(egressLane);
                    Integer matchingConnection = getMatchingSignalGroup(signalGroups, egressSignalGroups);
                    if(matchingConnection != null){
                        signalGroup = matchingConnection;
                    }else{
                        signalGroup = -1;
                    }
                }
            }else{
                signalGroup = -1;
            }
        }


        J2735MovementPhaseState signalState = getSignalGroupState(matchingSpat, signalGroup);

        var optCoreData = BsmUtils.getCoreData(bsm);

        StopLinePassageEvent event = new StopLinePassageEvent();
        event.setTimestamp(bsmTime);
        if (path.getIntersection() != null) {
            event.setRoadRegulatorID(path.getIntersection().getRoadRegulatorId());
            event.setIntersectionID(path.getIntersection().getIntersectionId());
        }
        event.setConnectionID(connectionId);
        event.setEventState(signalState);
        event.setIngressLane(ingressLane.getId());
        if (egressLane != null) {
            event.setEgressLane(egressLane.getId());
        }
        if (optCoreData.isPresent()) {
            var coreData = optCoreData.get();
            event.setVehicleID(coreData.getId());
            if (coreData.getPosition() != null) {
                event.setLongitude(coreData.getPosition().getLongitude().doubleValue());
                event.setLatitude(coreData.getPosition().getLatitude().doubleValue());
            }
            if (coreData.getHeading() != null) {
                event.setHeading(coreData.getHeading().doubleValue());
            }
            if (coreData.getSpeed() != null) {
                event.setSpeed(coreData.getSpeed().doubleValue());
            }
        }
        event.setSignalGroup(signalGroup);
        
        return event;
    }

    public J2735MovementPhaseState getSignalGroupState(ProcessedSpat spat, int signalGroup){
        for(MovementState state: spat.getStates()){
            if(state.getSignalGroup() == signalGroup && state.getStateTimeSpeed().size() > 0){
                return state.getStateTimeSpeed().get(0).getEventState();
            }
        }
        return null;
    }

    private Integer getMatchingSignalGroup(Set<Integer> ingressGroups, Set<Integer> egressGroups){
        for(Integer ingressGroupID: ingressGroups){
            for(Integer egressGroupID: egressGroups){
                if(ingressGroupID == egressGroupID){
                    return ingressGroupID;
                }
            }
        }
        return null;
    }

}
