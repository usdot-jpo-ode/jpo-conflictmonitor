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
import us.dot.its.jpo.conflictmonitor.monitor.utils.BsmUtils;
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

    private double minDistanceFeet;
    private double headingToleranceDegrees;

    public VehiclePath(BsmAggregator bsms, Intersection intersection, double minDistanceFeet, double headingToleranceDegrees){
        this.bsms = bsms;
        this.intersection = intersection;
        this.minDistanceFeet = minDistanceFeet;
        this.headingToleranceDegrees = headingToleranceDegrees;
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
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStopLines(), minDistanceFeet,
                headingToleranceDegrees);
        if(match != null){
            this.ingressLane = match.getLane();
            this.ingressBsm = match.getBsm();
        }
    }

    public void calculateEgress(){
        if (intersection.getStartLines() == null) return;
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStartLines(), minDistanceFeet,
                headingToleranceDegrees);
        if(match != null){
            this.egressLane = match.getLane();
            this.egressBsm = match.getBsm();
        }
    }


    /**
     * Find the stop or start line point that the vehicle path passes closest to
     * @param lines - List of ingress or egress lines with stop or start points
     * @param minDistanceFeet - Radius of the buffer at stop/start points to include BSMs
     * @param headingToleranceDegrees - Tolerance for heading to include BSMs
     * @return LineVehicleIntersection
     */
    public LineVehicleIntersection findLineVehicleIntersection(
            List<IntersectionLine> lines,
            final double minDistanceFeet,
            final double headingToleranceDegrees){

        final double minDistanceCM = CoordinateConversion.feetToCM(minDistanceFeet);
        double minDistance = Double.MAX_VALUE;
        OdeBsmData matchingBsm = null;
        IntersectionLine bestLine = null;


        for(IntersectionLine line : lines){
            if(this.pathPoints.isWithinDistance(line.getStopLinePoint(), minDistanceCM)){
                int index =0;
                for(OdeBsmData bsm : this.bsms.getBsms()){
                    Point p = this.pathPoints.getPointN(index);
                    var optionalHeading = BsmUtils.getHeading(bsm);
                    if (optionalHeading.isEmpty()) {
                        logger.warn("No heading found in BSM");
                        continue;
                    }
                    double vehicleHeading = optionalHeading.get();
                    if(CircleMath.getAngularDistanceDegrees(vehicleHeading, line.getHeading()) <= headingToleranceDegrees){
                        double distance = p.distance(line.getStopLinePoint());
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

        // Find all BSMs within the buffer distance of the stop point
        List<OdeBsmData> bsmList = new ArrayList<>();
        for (OdeBsmData bsm : this.bsms.getBsms()) {

        }

        if(bestLine != null){
            var lineVehicleIntersection = new LineVehicleIntersection(bestLine.getLane(), matchingBsm);
            lineVehicleIntersection.setBsmList(bsmList);
            return lineVehicleIntersection;
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
