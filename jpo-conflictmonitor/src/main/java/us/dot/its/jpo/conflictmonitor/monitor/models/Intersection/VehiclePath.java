package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.io.WKTWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

@Getter
@Setter
public class VehiclePath {

    private static final Logger logger = LoggerFactory.getLogger(VehiclePath.class);
    
    private LineString pathPoints;
    private BsmAggregator bsms;
    private Intersection intersection;
    private GeometryFactory geometryFactory;

    private Lane ingressLane;
    private Lane egressLane;
    private OdeBsmData ingressBsm;
    private OdeBsmData egressBsm;    

    public VehiclePath(BsmAggregator bsms, Intersection intersection){
        this.bsms = bsms;
        this.intersection = intersection;
        this.geometryFactory = new GeometryFactory();
         
        buildVehiclePath();
    }

    public void buildVehiclePath(){
        Coordinate referencePoint = this.intersection.getReferencePoint();

        if (referencePoint == null) {
            logger.error("Reference point is null");
            return;
        }
       
        Coordinate[] vehicleCoords = new Coordinate[bsms.getBsms().size()];
        int index =0;
        for(OdeBsmData bsm : bsms.getBsms()){
            
            OdePosition3D position = ((J2735Bsm)bsm.getPayload().getData()).getCoreData().getPosition();
            double[] shiftedPosition = CoordinateConversion.longLatToOffsetCM(
                position.getLongitude().doubleValue(),
                position.getLatitude().doubleValue(),
                referencePoint.getX(),
                referencePoint.getY()
            );
            vehicleCoords[index] = new Coordinate(shiftedPosition[0], shiftedPosition[1]);
            index++;
        }
        
        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(vehicleCoords);
        this.pathPoints = new LineString(sequence, this.geometryFactory);
        this.calculateIngress();
        this.calculateEgress();
    }

    public void calculateIngress(){
        if (intersection.getStopLines() == null) return;
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStopLines());
        if(match != null){
            this.ingressLane = match.getLane();
            this.ingressBsm = match.getBsm();
        }
    }

    public void calculateEgress(){
        if (intersection.getStartLines() == null) return;
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStartLines());
        if(match != null){
            this.egressLane = match.getLane();
            this.egressBsm = match.getBsm();
        }
    }


    public LineVehicleIntersection findLineVehicleIntersection(List<IntersectionLine> lines){
        double minDistance = Double.MAX_VALUE;
        OdeBsmData matchingBsm = null;
        IntersectionLine bestLine = null;

        for(IntersectionLine line : lines){
            if(this.pathPoints.isWithinDistance(line.getCenterPoint(), 450)){
                int index =0;
                for(OdeBsmData bsm : this.bsms.getBsms()){
                    Point p = this.pathPoints.getPointN(index);
                    double vehicleHeading = ((J2735Bsm)bsm.getPayload().getData()).getCoreData().getHeading().doubleValue();
                    if(CircleMath.getAngularDistanceDegrees(vehicleHeading, line.getHeading()) <= 20){
                        double distance = p.distance(line.getCenterPoint());
                        if(distance < minDistance){
                            matchingBsm = bsm;
                            minDistance = distance;
                            bestLine = line;
                        }
                    }
                    index++;
                }
            }
        }

        if(bestLine != null){
            return new LineVehicleIntersection(bestLine.getLane(), matchingBsm);
        } else{
            return null;
        }
    }
    

    
    public String getVehiclePathAsWkt() {
        WKTWriter writer = new WKTWriter(2);
        String wtkOut = "wtk\n";
        writer.setFormatted(true);

        wtkOut += "\"" + writer.writeFormatted(this.pathPoints) + "\"\n";

        return wtkOut;
    }

    @Override
    public String toString(){
        return "Vehicle Path: Points: " + this.pathPoints.getNumPoints();
    }

}
