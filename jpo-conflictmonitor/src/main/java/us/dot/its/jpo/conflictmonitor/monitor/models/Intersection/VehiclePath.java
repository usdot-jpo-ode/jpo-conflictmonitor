package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.geotools.geometry.GeometryBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

import us.dot.its.jpo.conflictmonitor.monitor.models.bsm.BsmAggregator;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.ode.model.OdeBsmData;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

public class VehiclePath {
    
    private LineString pathPoints;
    private BsmAggregator bsms;
    private Intersection intersection;
    private GeometryFactory geometryFactory;

    

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
            OdePosition3D position = ((J2735BsmCoreData)bsm.getPayload().getData()).getPosition();
            double[] shiftedPosition = CoordinateConversion.longLatToOffsetM(
                position.getLongitude().doubleValue(),
                position.getLatitude().doubleValue(),
                this.intersection.getReferencePoint().getX(),
                this.intersection.getReferencePoint().getY()
            );

            vehicleCoords[index] = new Coordinate(shiftedPosition[0], shiftedPosition[1]);
            index++;
        }
        
        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(vehicleCoords);
        this.pathPoints = new LineString(sequence, this.geometryFactory);
        
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

    @Override
    public String toString(){
        return "Vehicle Path";
    }

}
