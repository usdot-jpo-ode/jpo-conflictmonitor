package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.io.WKTWriter;

import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapFeature;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapNode;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.map.MapProperties;



public class Lane {

    private int id;
    private LineString points;
    private Boolean ingress;

    private GeometryFactory geometryFactory;
    
    private int laneWidthCm;
    private int region;

    public static Lane fromGeoJsonFeature(MapFeature<us.dot.its.jpo.geojsonconverter.pojos.geojson.LineString> feature, Coordinate referencePoint, int laneWidthCm){
        
        Lane lane = new Lane();
        lane.setLaneWidthCm(laneWidthCm);
        if (feature.getId() != null) {
            lane.setId(feature.getId().intValue());
        }
        

        List<MapNode> nodes = ((MapProperties)feature.getProperties()).getNodes();
        Coordinate[] coordinates = new Coordinate[nodes.size()];

        int sumX = 0;
        int sumY = 0;
        for(int i=0; i< nodes.size(); i++){
            int deltaX = nodes.get(i).getDelta()[0];
            int deltaY = nodes.get(i).getDelta()[1];

            sumX += deltaX;
            sumY += deltaY;

            coordinates[i] = new Coordinate(sumX, sumY);
        }

        PackedCoordinateSequence.Double sequence = new PackedCoordinateSequence.Double(coordinates);
        LineString lanePoints = new LineString(sequence, lane.getGeometryFactory());

        if(((MapProperties)feature.getProperties()).getIngressPath()){
            lane.setIngress(true);
        }
        else{
            lane.setIngress(false);
            lanePoints = lanePoints.reverse();
        }



        lane.setPoints(lanePoints);
        lane.region = -1;

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
            LaneSegment segment = new LaneSegment(segmentStartPoint, segmentEndPoint, this.laneWidthCm, this.ingress, this.geometryFactory);
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

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
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
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Lane)) {
            return false;
        }
        Lane lane = (Lane) o;
        return Objects.equals(this.getId(), lane.getId());
    }

    @Override
    public String toString(){
        return "Lane: " + this.getId();
    }
    
}
