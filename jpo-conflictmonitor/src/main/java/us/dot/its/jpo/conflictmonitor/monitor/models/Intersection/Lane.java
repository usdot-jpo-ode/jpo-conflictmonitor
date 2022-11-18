package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeature;

public class Lane {

    private int id;
    private LineString points;
    private Boolean ingress;
    private GeometryFactory geometryFactory;

    public static Lane fromGeoJsonFeature(MapFeature feature, Coordinate referencePoint){
        
        Lane lane = new Lane();
        lane.setId(feature.getId());
        if(feature.getProperties().getIngressPath()){
            lane.setIngress(true);
        }
        else{
            lane.setIngress(false);
        }

        double[][] featureCoordinates = feature.getGeometry().getCoordinates();
        Coordinate[] coordinates = new Coordinate[featureCoordinates.length];
        for(int i=0; i< featureCoordinates.length; i++){
            double[] shiftedCoordinate = CoordinateConversion.longLatToOffsetCM(
                featureCoordinates[i][0], 
                featureCoordinates[i][1],
                referencePoint.getX(),
                referencePoint.getY());

            coordinates[i] = new Coordinate(shiftedCoordinate[0], shiftedCoordinate[1]);
        }



        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(coordinates);
        LineString lanePoints = new LineString(sequence, lane.getGeometryFactory());
        lane.setPoints(lanePoints);
        


        return lane;
    }

    public Lane(){
        geometryFactory = new GeometryFactory();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LineString getPoints() {
        return points;
    }

    public void setPoints(LineString points) {
        this.points = points;
    }

    public Boolean getIngress() {
        return ingress;
    }

    public void setIngress(Boolean ingress) {
        this.ingress = ingress;
    }

    public GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    public void setGeometryFactory(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    @Override
    public String toString(){
        return "Lane: " + this.getId();
    }
    
}
