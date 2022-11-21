package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.geotools.geometry.GeometryBuilder;
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
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
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
        Coordinate referencePoint = intersection.getReferencePoint();
       
        Coordinate[] vehicleCoords = new Coordinate[bsms.getBsms().size()];
        int index =0;
        for(OdeBsmData bsm : bsms.getBsms()){
            
            OdePosition3D position = ((J2735Bsm)bsm.getPayload().getData()).getCoreData().getPosition();
            double[] shiftedPosition = CoordinateConversion.longLatToOffsetCM(
                position.getLongitude().doubleValue(),
                position.getLatitude().doubleValue(),
                this.intersection.getReferencePoint().getX(),
                this.intersection.getReferencePoint().getY()
            );
            
            vehicleCoords[index] = new Coordinate(shiftedPosition[0], shiftedPosition[1]);
            //System.out.println("BSM Time:" + bsm.getMetadata().getRecordGeneratedAt() + " offsetX :" +vehicleCoords[index].getX()+ " offsetY: "+vehicleCoords[index].getY());
            index++;
        }
        
        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(vehicleCoords);
        this.pathPoints = new LineString(sequence, this.geometryFactory);
        this.calculateIngress(); 
    }

    public void calculateIngress(){
        double minDistance = Double.MAX_VALUE;
        OdeBsmData matchingBsm = null;
        StopLine bestStopLine = null;


        for(StopLine stop : intersection.getStopLines()){
            if(this.pathPoints.isWithinDistance(stop.getCenterPoint(), 450)){
                int index =0;
                for(OdeBsmData bsm : this.bsms.getBsms()){
                    Point p = this.pathPoints.getPointN(index);
                    double vehicleHeading = ((J2735Bsm)bsm.getPayload().getData()).getCoreData().getHeading().doubleValue();
                    if(CircleMath.getAngularDistanceDegrees(vehicleHeading, stop.getHeading()) <= 20){
                        double distance = p.distance(stop.getCenterPoint());
                        if(distance < minDistance){
                            matchingBsm = bsm;
                            minDistance = distance;
                            bestStopLine = stop;
                        }
                    }
                    index++;
                }
            }
        }

        if(bestStopLine != null){
            this.ingressLane = bestStopLine.getLane();
            this.ingressBsm = matchingBsm;
        } else{
            System.out.println("BSM Set did not cross intersection");
        }

    }

    

    public int getEgressLane(){
        return 0;
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
