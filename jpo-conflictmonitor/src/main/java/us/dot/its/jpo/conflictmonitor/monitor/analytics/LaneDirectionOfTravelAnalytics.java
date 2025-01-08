package us.dot.its.jpo.conflictmonitor.monitor.analytics;


import static us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelConstants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelAlgorithm;
import us.dot.its.jpo.conflictmonitor.monitor.algorithms.lane_direction_of_travel.LaneDirectionOfTravelParameters;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneSegment;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmTimestampExtractor;
import us.dot.its.jpo.conflictmonitor.monitor.models.events.LaneDirectionOfTravelEvent;
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.conflictmonitor.monitor.utils.MathFunctions;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm.ProcessedBsm;

@Component(DEFAULT_LANE_DIRECTION_OF_TRAVEL_ALGORITHM)
public class LaneDirectionOfTravelAnalytics implements LaneDirectionOfTravelAlgorithm{
    
    LaneDirectionOfTravelParameters parameters;

    

    @Override
    public ArrayList<LaneDirectionOfTravelEvent> getLaneDirectionOfTravelEvents(LaneDirectionOfTravelParameters parameters, VehiclePath path){
        this.parameters = parameters;

        Lane ingressLane = path.getIngressLane();
        Lane egressLane = path.getEgressLane();
        
        HashMap<LaneSegment, ArrayList<Integer>> ingressBsmMap = getBsmsByLaneSegment(path, ingressLane);
        HashMap<LaneSegment, ArrayList<Integer>> egressBsmMap = getBsmsByLaneSegment(path, egressLane);


        ArrayList<LaneDirectionOfTravelEvent> ingressEvents = getLaneDirectionEvents(path, ingressLane, ingressBsmMap);
        ArrayList<LaneDirectionOfTravelEvent> egressEvents = getLaneDirectionEvents(path, egressLane, egressBsmMap);

        ingressEvents.addAll(egressEvents);

        return ingressEvents;
    }

    // Returns a HashMap mapping Lane Segments objects to a List of BSM's that correspond to each segment
    protected HashMap<LaneSegment, ArrayList<Integer>> getBsmsByLaneSegment(VehiclePath path, Lane lane){

        HashMap<LaneSegment, ArrayList<Integer>> segmentBsmMap = new HashMap<LaneSegment, ArrayList<Integer>>();

        // If the lane has no geometry return an empty map.
        if(lane == null){
            return segmentBsmMap;
        }

        ArrayList<LaneSegment> segments = lane.getLaneSegmentPolygons();
        LineString pathPoints = path.getPathPoints();

        for(int i =0; i < pathPoints.getNumPoints(); i++){
            Point pathPoint = pathPoints.getPointN(i);

            for(LaneSegment segment: segments){
                if(segment.getPolygon().contains(pathPoint)){
                    
                    // Add BSM to existing list if available, otherwise create a new list to store the BSM
                    if(segmentBsmMap.get(segment) != null){
                        segmentBsmMap.get(segment).add(i);
                    }else{
                        ArrayList<Integer> newList = new ArrayList<>();
                        newList.add(i);
                        segmentBsmMap.put(segment, newList);
                    }
                }
            }
        }

        return segmentBsmMap;
    }

    protected double[] getSegmentPointAsLongLat(VehiclePath path, Point p){
        Coordinate reference = path.getIntersection().getReferencePoint();
        return CoordinateConversion.offsetCmToLongLat(reference.getX(), reference.getY(), p.getX(), p.getY());
    }


    protected ArrayList<LaneDirectionOfTravelEvent> getLaneDirectionEvents(VehiclePath path, Lane lane, HashMap<LaneSegment, ArrayList<Integer>> segmentBsmMap){

        ArrayList<LaneDirectionOfTravelEvent> laneEvents = new ArrayList<>();

        for(LaneSegment segment : segmentBsmMap.keySet()){
            ArrayList<Integer> bsmIndecies = segmentBsmMap.get(segment);

            ArrayList<Double> headings = new ArrayList<>();
            ArrayList<Double> offsetDistances = new ArrayList<>();
            ArrayList<Long> times = new ArrayList<>();

            if(bsmIndecies.size() > this.parameters.getMinimumPointsPerSegment()){
                for(Integer index : bsmIndecies){
                    ProcessedBsm<us.dot.its.jpo.geojsonconverter.pojos.geojson.Point> bsm = path.getBsms().getBsms().get(index);
                    Point pathPoint = path.getPathPoints().getPointN(index);
                    double distance = pathPoint.distance(segment.getCenterLine());
                    
                    Optional<Double> optHeading = BsmUtils.getHeading(bsm);
                    if (optHeading.isPresent()) {
                        headings.add(optHeading.get());
                    } else {
                        headings.add(null);
                    }
                    offsetDistances.add(distance);
                    times.add(BsmTimestampExtractor.getBsmTimestamp(bsm));
                    
                }

                double medianHeading = MathFunctions.getMedian(headings);
                double medianDistance = MathFunctions.getMedian(offsetDistances);
                long medianTimestamp = MathFunctions.getMedianTimestamp(times);


                double[] startLongLat = getSegmentPointAsLongLat(path, segment.getStartPoint());
                double[] endLongLat = getSegmentPointAsLongLat(path, segment.getEndPoint());

            

                // Build Resulting Event based upon the aggregate results
                LaneDirectionOfTravelEvent event = new LaneDirectionOfTravelEvent();
                event.setTimestamp(medianTimestamp);
                event.setRoadRegulatorID(lane.getRegion());
                event.setIntersectionID(path.getIntersection().getIntersectionId());
                event.setLaneID(lane.getId());
                event.setLaneSegmentNumber(segment.getSegmentID());
                

                event.setLaneSegmentInitialLongitude(startLongLat[0]);
                event.setLaneSegmentInitialLatitude(startLongLat[1]);
                event.setLaneSegmentFinalLongitude(endLongLat[0]);
                event.setLaneSegmentFinalLatitude(endLongLat[1]);

                event.setExpectedHeading(segment.getHeading());
                event.setMedianVehicleHeading(medianHeading);
                event.setMedianDistanceFromCenterline(medianDistance);
                event.setAggregateBSMCount(bsmIndecies.size());


                



                
                laneEvents.add(event);
            }
        }

        return laneEvents;
    }
}
