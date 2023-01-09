package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.io.WKTWriter;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

public class VehiclePath {
    
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

            //System.out.println("Reference Long:" + referencePoint.getX() +"Reference Lat: " + referencePoint.getY() + "Bsm Long:" + position.getLongitude() + "BSM Lat: " + position.getLatitude() + "Shifted Position: " + shiftedPosition[0] + "," + shiftedPosition[1]);
            
            vehicleCoords[index] = new Coordinate(shiftedPosition[0], shiftedPosition[1]);
            index++;
        }
        
        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(vehicleCoords);
        this.pathPoints = new LineString(sequence, this.geometryFactory);
        this.calculateIngress();
        this.calculateEgress();
    }

    public void calculateIngress(){
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStopLines(), bsms);
        if(match != null){
            this.ingressLane = match.getLane();
            this.ingressBsm = match.getBsm();
        }
    }

    public void calculateEgress(){
        LineVehicleIntersection match = findLineVehicleIntersection(this.intersection.getStartLines(), bsms);
        if(match != null){
            this.egressLane = match.getLane();
            this.egressBsm = match.getBsm();
        }
    }


    public LineVehicleIntersection findLineVehicleIntersection(ArrayList<IntersectionLine> lines, BsmAggregator bsms){
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
    
    public Lane getIngressLane() {
        return ingressLane;
    }

    public void setIngressLane(Lane ingressLane) {
        this.ingressLane = ingressLane;
    }

    public Lane getEgressLane() {
        return egressLane;
    }

    public void setEgressLane(Lane egressLane) {
        this.egressLane = egressLane;
    }

    public OdeBsmData getIngressBsm() {
        return ingressBsm;
    }

    public void setIngressBsm(OdeBsmData ingressBsm) {
        this.ingressBsm = ingressBsm;
    }

    public OdeBsmData getEgressBsm() {
        return egressBsm;
    }

    public void setEgressBsm(OdeBsmData egressBsm) {
        this.egressBsm = egressBsm;
    }

    public LineString getPathPoints() {
        return pathPoints;
    }

    public void setPathPoints(LineString pathPoints) {
        this.pathPoints = pathPoints;
    }

    public BsmAggregator getBsms() {
        return bsms;
    }

    public void setBsms(BsmAggregator bsms) {
        this.bsms = bsms;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }

    public GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    public void setGeometryFactory(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
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
