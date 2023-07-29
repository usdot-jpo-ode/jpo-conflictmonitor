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

import java.time.Duration;
import java.util.List;
import java.util.Optional;


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

        // Find stop events within the stopping search distance along the ingress lane.
        List<OdeBsmData> bsmList = path.findBsmsInIngressLane(ingressLane, parameters.getUpstreamSearchDistance());
        List<OdeBsmData> stoppedBsmList = path.filterStoppedBsms(bsmList, parameters.getStopSpeedThreshold());
        if (stoppedBsmList.size() < 2) {
            logger.info("Fewer than 2 stopped BSMs found for path {}, can't generate StopLineStop event", path);
            return null;
        }
        OdeBsmData firstStoppedBsm = stoppedBsmList.get(0);
        OdeBsmData lastStoppedBsm = stoppedBsmList.get(stoppedBsmList.size() - 1);

        long firstTimestamp = BsmTimestampExtractor.getBsmTimestamp(firstStoppedBsm);
        long lastTimestamp = BsmTimestampExtractor.getBsmTimestamp(lastStoppedBsm);
        long durationStoppedMillis = Math.abs(lastTimestamp - firstTimestamp);
        logger.info("Stop duration {} ms", durationStoppedMillis);
        double minTimeStoppedSeconds = parameters.getMinTimeStopped();
        long minTimeStoppedMillis = Math.round(minTimeStoppedSeconds * 1000);
        if (durationStoppedMillis < minTimeStoppedMillis) {
            logger.warn("BSMs stopped for {} milliseconds, less than the minimum time of {} ms required to generate a StopLineStop event", durationStoppedMillis, minTimeStoppedMillis);
            return null;
        }

        ProcessedSpat firstSpat = spats.getSpatAtTime(firstTimestamp);
        ProcessedSpat lastSpat = spats.getSpatAtTime(lastTimestamp);


        LaneConnection connection = path.getIntersection().getLaneConnection(ingressLane, egressLane);

        J2735MovementPhaseState firstSignalState = getSignalGroupState(firstSpat, connection.getSignalGroup());
        J2735MovementPhaseState lastSignalState = getSignalGroupState(lastSpat, connection.getSignalGroup());


        

        StopLineStopEvent event = new StopLineStopEvent();
        event.setIntersectionID(path.getIntersection().getIntersectionId());
        event.setRoadRegulatorID(path.getIntersection().getRoadRegulatorId());
        event.setIngressLane(ingressLane.getId());
        if (egressLane != null) {
            event.setEgressLane(egressLane.getId());
        }
        Optional<Double> optionalHeading = BsmUtils.getHeading(firstStoppedBsm);
        if (optionalHeading.isPresent()) {
            event.setHeading(optionalHeading.get());
        }
        event.setInitialTimestamp(firstTimestamp);
        event.setInitialEventState(firstSignalState);
        event.setFinalTimestamp(lastTimestamp);
        event.setFinalEventState(lastSignalState);
        
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
