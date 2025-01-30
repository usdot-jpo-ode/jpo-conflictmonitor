package us.dot.its.jpo.conflictmonitor.monitor.analytics;

import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.stop_line_stop.StopLineStopConstants.*;
import static us.dot.its.jpo.conflictmonitor.monitor.utils.SpatUtils.getSignalGroupState;

import org.locationtech.jts.geom.CoordinateXY;
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
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.conflictmonitor.monitor.utils.SpatUtils;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;
import us.dot.its.jpo.geojsonconverter.pojos.spat.ProcessedSpat;
import us.dot.its.jpo.ode.plugin.j2735.J2735MovementPhaseState;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        List<ProcessedBsm<Point>> bsmList = path.findBsmsInIngressLane(ingressLane, parameters.getUpstreamSearchDistance());
        List<ProcessedBsm<Point>> stoppedBsmList = path.filterStoppedBsms(bsmList, parameters.getStopSpeedThreshold());
        if (stoppedBsmList.size() < 2) {
            logger.info("Fewer than 2 stopped BSMs found for path {}, can't generate StopLineStop event", path);
            return null;
        }
        ProcessedBsm<Point> firstStoppedBsm = stoppedBsmList.getFirst();
        ProcessedBsm<Point> lastStoppedBsm = stoppedBsmList.getLast();

        long firstTimestamp = BsmTimestampExtractor.getBsmTimestamp(firstStoppedBsm);
        logger.info("First stopped BSM timestamp {}", firstTimestamp);
        long lastTimestamp = BsmTimestampExtractor.getBsmTimestamp(lastStoppedBsm);
        logger.info("Last stopped BSM timestamp {}", lastTimestamp);
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
        if (firstSpat == null || lastSpat == null) {
            logger.warn("Can't generate StopLineStop event because SPATs not found.");
            return null;
        }



        StopLineStopEvent event = new StopLineStopEvent();
        String vehicleId = BsmUtils.getVehicleId(firstStoppedBsm);
        event.setVehicleID(vehicleId);

        event.setInitialTimestamp(firstTimestamp);
        event.setFinalTimestamp(lastTimestamp);

        event.setIntersectionID(path.getIntersection().getIntersectionId());
        event.setRoadRegulatorID(path.getIntersection().getRoadRegulatorId());
        event.setIngressLane(ingressLane.getId());
        if (egressLane != null) {
            event.setEgressLane(egressLane.getId());
        }else{
            event.setEgressLane(-1);
        }
        Optional<Double> optionalHeading = BsmUtils.getHeading(firstStoppedBsm);
        if (optionalHeading.isPresent()) {
            event.setHeading(optionalHeading.get());
        }
        CoordinateXY firstBsmPosition = BsmUtils.getPosition(firstStoppedBsm);
        event.setLongitude(firstBsmPosition.getX());
        event.setLatitude(firstBsmPosition.getY());


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

        if (signalGroup > -1) {
            event.setSignalGroup(signalGroup);
            J2735MovementPhaseState firstSignalState = getSignalGroupState(firstSpat, signalGroup);
            J2735MovementPhaseState lastSignalState = getSignalGroupState(lastSpat, signalGroup);
            event.setInitialEventState(firstSignalState);
            event.setFinalEventState(lastSignalState);
            event.setConnectionID(connectionId);


            List<ProcessedSpat> filteredSpats = SpatUtils.filterSpatsByTimestamp(spats.getSpats(), firstTimestamp, lastTimestamp);

            // String spatDesc = SpatUtils.describeSpats(filteredSpats, signalGroup);
            SpatUtils.SpatStatistics spatStatistics = SpatUtils.getSpatStatistics(filteredSpats, signalGroup);

            event.setTimeStoppedDuringRed(spatStatistics.getTimeStoppedDuringRed());
            event.setTimeStoppedDuringYellow(spatStatistics.getTimeStoppedDuringYellow());
            event.setTimeStoppedDuringGreen(spatStatistics.getTimeStoppedDuringGreen());
            event.setTimeStoppedDuringDark(spatStatistics.getTimeStoppedDuringDark());
        }else{
            event.setTimeStoppedDuringRed(-1);
            event.setTimeStoppedDuringYellow(-1);
            event.setTimeStoppedDuringGreen(-1);
            event.setTimeStoppedDuringDark(-1);
            event.setSignalGroup(-1);
            event.setInitialEventState(null);
            event.setFinalEventState(null);
        }

        logger.info("StopLineStopEvent: {}", event);
        return event;
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
