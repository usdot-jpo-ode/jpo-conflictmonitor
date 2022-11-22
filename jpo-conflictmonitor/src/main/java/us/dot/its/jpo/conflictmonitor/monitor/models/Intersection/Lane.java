package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.io.WKTWriter;

import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;
import us.dot.its.jpo.conflictmonitor.monitor.utils.CoordinateConversion;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeature;

public class Lane {

    private int id;
    private LineString points;
    private Boolean ingress;
    private GeometryFactory geometryFactory;
    private int laneWidthCm;

    

    public static Lane fromGeoJsonFeature(MapFeature feature, Coordinate referencePoint, int laneWidthCm){
        
        Lane lane = new Lane();
        lane.setLaneWidthCm(laneWidthCm);
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

    public ArrayList<LaneSegment> getLaneSegmentPolygons(){
        
        ArrayList<LaneSegment> laneSegments = new ArrayList<>();
        for(int i=1; i < this.points.getNumPoints(); i++){
            Point segmentStartPoint = this.points.getPointN(i-1);
            Point segmentEndPoint = this.points.getPointN(i);

            double headingRadians = Math.atan2(segmentStartPoint.getY() - segmentEndPoint.getY(),segmentStartPoint.getX() - segmentEndPoint.getX());
            double headingFromNorth = CircleMath.headingXYToHeadingFromNorth(Math.toDegrees(headingRadians));
            double positiveHeadingShift = headingRadians + Math.PI / 2.0;
            double negativeHeadingShift = headingRadians - Math.PI / 2.0;

            double offsetDistance = this.laneWidthCm / 2.0;

            Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(
                    segmentStartPoint.getX() + (Math.cos(positiveHeadingShift) * offsetDistance),
                    segmentStartPoint.getY() + (Math.sin(positiveHeadingShift) * offsetDistance)
                ),
                new Coordinate(
                    segmentEndPoint.getX() + (Math.cos(positiveHeadingShift) * offsetDistance),
                    segmentEndPoint.getY() + (Math.sin(positiveHeadingShift) * offsetDistance)
                ),
                new Coordinate(
                    segmentEndPoint.getX() + (Math.cos(negativeHeadingShift) * offsetDistance),
                    segmentEndPoint.getY() + (Math.sin(negativeHeadingShift) * offsetDistance)
                ),
                new Coordinate(
                    segmentStartPoint.getX() + (Math.cos(negativeHeadingShift) * offsetDistance),
                    segmentStartPoint.getY() + (Math.sin(negativeHeadingShift) * offsetDistance)
                ),
                new Coordinate(
                    segmentStartPoint.getX() + (Math.cos(positiveHeadingShift) * offsetDistance),
                    segmentStartPoint.getY() + (Math.sin(positiveHeadingShift) * offsetDistance)
                ),
            };

            Polygon poly = this.geometryFactory.createPolygon(coordinates);
            
            LaneSegment segment = new LaneSegment(poly, headingFromNorth);
            laneSegments.add(segment);
        }


        return laneSegments;
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

    public int getLaneWidthCm() {
        return laneWidthCm;
    }

    public void setLaneWidthCm(int laneWidthCm) {
        this.laneWidthCm = laneWidthCm;
    }

    public String getLaneAsWkt() {
        WKTWriter writer = new WKTWriter(2);
        String wktOut = "wkt\n";
        writer.setFormatted(true);

        ArrayList<LaneSegment> segments = this.getLaneSegmentPolygons();
        for(LaneSegment segment : segments){
            wktOut += "\"" + writer.writeFormatted(segment.getPolygon()) + "\"\n";
        }
        return wktOut;
    }

    @Override
    public String toString(){
        return "Lane: " + this.getId();
    }
    
}
