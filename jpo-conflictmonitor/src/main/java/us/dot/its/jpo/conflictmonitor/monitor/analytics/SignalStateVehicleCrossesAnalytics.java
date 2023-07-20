package us.dot.its.jpo.conflictmonitor.monitor.analytics;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesConstants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.MonitorServiceController;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.signal_state_vehicle_crosses.SignalStateVehicleCrossesParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneConnection;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.StopLinePassageEvent;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.models.spat.SpatTimestampExtractor;
import us.dot.its.jpo.geojsonconverter.pojos.spat.MovementState;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.Set;

@Component(DEFAULT_SIGNAL_STATE_VEHICLE_CROSSES_ALGORITHM)
public class SignalStateVehicleCrossesAnalytics implements SignalStateVehicleCrossesAlgorithm{

    private static final Logger logger = LoggerFactory.getLogger(SignalStateVehicleCrossesAnalytics.class);
  

    @Override
    public StopLinePassageEvent getSignalStateEvent(SignalStateVehicleCrossesParameters parameters, VehiclePath path, SpatAggregator spats){
        

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
        if (egressLane != null) {
            LaneConnection connection = path.getIntersection().getLaneConnection(ingressLane, egressLane);
            signalGroup = connection.getSignalGroup();
            connectionId = connection.getConnectionId();
        } else {
            Set<Integer> signalGroups = path.getIntersection().getSignalGroupsForIngressLane(ingressLane);
            if (signalGroups.size() == 1) {
                signalGroup = signalGroups.iterator().next();
            } else {
                logger.info("No egress lane found for path {}, and ingress lane {} has multiple signal groups {}, can't determine signalGroup to generate StopLinePassage event", path, ingressLane, signalGroups);
                return null;
            }
        }

        J2735MovementPhaseState signalState = getSignalGroupState(matchingSpat, signalGroup);

        J2735Bsm bsmData = (J2735Bsm)bsm.getPayload().getData();

        StopLinePassageEvent event = new StopLinePassageEvent();
        event.setTimestamp(bsmTime);
        event.setRoadRegulatorID(path.getIntersection().getRoadRegulatorId());
        event.setIntersectionID(path.getIntersection().getIntersectionId());
        event.setConnectionID(connectionId);
        event.setEventState(signalState);
        event.setIngressLane(ingressLane.getId());
        if (egressLane != null) {
            event.setEgressLane(egressLane.getId());
        }
        event.setVehicleID(bsmData.getCoreData().getId());
        event.setLongitude(bsmData.getCoreData().getPosition().getLongitude().doubleValue());
        event.setLatitude(bsmData.getCoreData().getPosition().getLongitude().doubleValue());
        event.setHeading(bsmData.getCoreData().getHeading().doubleValue());
        event.setSpeed(bsmData.getCoreData().getSpeed().doubleValue());
        event.setSignalGroup(signalGroup);
        
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
