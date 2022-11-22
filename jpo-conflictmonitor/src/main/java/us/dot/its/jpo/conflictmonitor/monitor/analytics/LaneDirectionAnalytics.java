package us.dot.its.jpo.conflictmonitor.monitor.analytics;

import java.util.ArrayList;
import java.util.HashMap;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.Lane;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneSegment;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.VehiclePath;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.conflictmonitor.monitor.utils.MathFunctions;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

public class LaneDirectionAnalytics {
    
    private VehiclePath path;

    public LaneDirectionAnalytics(VehiclePath path){
        this.path = path;
    }

    public void start(){
        Lane ingressLane = this.path.getIngressLane();
        if(ingressLane != null){
            ArrayList<LaneSegment> segments = ingressLane.getLaneSegmentPolygons();
            

            HashMap<LaneSegment, ArrayList<OdeBsmData>> segmentBsmMap = new HashMap<LaneSegment, ArrayList<OdeBsmData>>();
            Coordinate referencePoint = this.path.getIntersection().getReferencePoint();
            GeometryFactory factory = ingressLane.getGeometryFactory();
            LineString pathPoints = this.path.getPathPoints();

            System.out.println("Lane Geometry");
            System.out.println(ingressLane.getLaneAsWkt());

            for(int i =0; i < pathPoints.getNumPoints(); i++){
                Point pathPoint = pathPoints.getPointN(i);
                // J2735BsmCoreData core = ((J2735Bsm)bsm.getPayload().getData()).getCoreData();
                
                for(LaneSegment segment: segments){
                    if(segment.getPolygon().contains(pathPoint)){
                        
                        if(segmentBsmMap.get(segment) != null){
                            segmentBsmMap.get(segment).add(this.path.getBsms().getBsms().get(i));
                        }else{
                            ArrayList<OdeBsmData> newList = new ArrayList<>();
                            newList.add(this.path.getBsms().getBsms().get(i));
                            segmentBsmMap.put(segment, newList);
                        }
                    }
                }
            }

            for(LaneSegment segment : segmentBsmMap.keySet()){
                ArrayList<OdeBsmData> bsms = segmentBsmMap.get(segment);

                ArrayList<Double> headings = new ArrayList();
                if(bsms.size() > 5){
                    for(OdeBsmData bsm : bsms){
                        headings.add(((J2735Bsm)bsm.getPayload().getData()).getCoreData().getHeading().doubleValue());
                    }
                    double heading = MathFunctions.getMedian(headings);
                    System.out.println("Segment Heading:" + segment.getHeading() + "Vehicle Heading: " + heading);
                }

            }

        }
        
    }

    
}
