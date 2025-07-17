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


/**
 * The Lane class contains all the geometry needed for performing lane based calculations on CV data near the intersection. 
 */
public class Lane {

    /**
     * int representing the lane ID within the intersection. This matches the lane ID from the MAP message
     */
    private int id;

    /**
     * LineString of points that define a given lane
     */
    private LineString points;

    /**
     * Boolean indicating if this this is an ingress lane or an egress lane. Set to true for an ingress lane.
     */
    private Boolean ingress;

    /**
     * GeometryFactory object used for constructing jts geometry 
     */
    private GeometryFactory geometryFactory;
    
    /**
     * Width of the lane in centimeters
     */
    private int laneWidthCm;

    /**
     * the region or road regulator ID associated with this lane. This field is no longer used and can be left unused or set to -1.
     */
    private int region;

    /**
     * Creates a Lane object from the Geojson features of the ProcessedMap message
     * @param feature
     * @param referencePoint
     * @param laneWidthCm
     * @return a new Lane object based upon the GeoJson of the Supplied Feature
     */
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

    /**
     * Creates an ArrayList of LaneSegment objects for this lane.
     * @return 
     */
    public ArrayList<LaneSegment> getLaneSegmentPolygons(){
        
        ArrayList<LaneSegment> laneSegments = new ArrayList<>();
        for(int i=1; i < this.points.getNumPoints(); i++){
            Point segmentStartPoint = this.points.getPointN(i-1);
            Point segmentEndPoint = this.points.getPointN(i);
            LaneSegment segment = new LaneSegment(segmentStartPoint, segmentEndPoint, this.laneWidthCm, this.ingress, i, this.geometryFactory);
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

    /**
     * Gets a representation of this lane using the WKT message format
     * @return String representing the lane geometry in the WKT format.
     */
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
