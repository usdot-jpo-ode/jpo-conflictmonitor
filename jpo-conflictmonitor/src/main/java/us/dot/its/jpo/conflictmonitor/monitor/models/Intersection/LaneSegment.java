package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;

public class LaneSegment {
    private final Polygon polygon;
    private final Point startPoint;
    private final Point endPoint;
    private final LineString centerLine;
    private final double heading;
    private final double laneWidth;
    private final int segmentID;

    

    public LaneSegment(Point startPoint, Point endPoint, double laneWidthCm, boolean ingress, int segmentID, GeometryFactory factory) {
        double headingRadians = 0;

        headingRadians = Angle.angle(endPoint.getCoordinate(), startPoint.getCoordinate());
        
        
        // It should no longer be required to flip egress lane direction. This is flipped when the lane is originally parsed from the map.
        // if(ingress){
        //     //headingRadians = Math.atan2(startPoint.getY() - endPoint.getY(),startPoint.getX() - endPoint.getX());
        //     headingRadians = Angle.angle(endPoint.getCoordinate(), startPoint.getCoordinate());
        // }else{
        //     //headingRadians = Math.atan2(endPoint.getY() - startPoint.getY(),endPoint.getX() - startPoint.getX());
        //     headingRadians = Angle.angle(startPoint.getCoordinate(), endPoint.getCoordinate());
        // }
        
        double headingFromNorth = CircleMath.headingXYToHeadingFromNorth(Math.toDegrees(headingRadians));
        double positiveHeadingShift = headingRadians + Math.PI / 2.0;
        double negativeHeadingShift = headingRadians - Math.PI / 2.0;

        double offsetDistance = laneWidthCm / 2.0;

        Coordinate[] polyCoordinates = new Coordinate[]{
            new Coordinate(
                startPoint.getX() + (Math.cos(positiveHeadingShift) * offsetDistance),
                startPoint.getY() + (Math.sin(positiveHeadingShift) * offsetDistance)
            ),
            new Coordinate(
                endPoint.getX() + (Math.cos(positiveHeadingShift) * offsetDistance),
                endPoint.getY() + (Math.sin(positiveHeadingShift) * offsetDistance)
            ),
            new Coordinate(
                endPoint.getX() + (Math.cos(negativeHeadingShift) * offsetDistance),
                endPoint.getY() + (Math.sin(negativeHeadingShift) * offsetDistance)
            ),
            new Coordinate(
                startPoint.getX() + (Math.cos(negativeHeadingShift) * offsetDistance),
                startPoint.getY() + (Math.sin(negativeHeadingShift) * offsetDistance)
            ),
            new Coordinate(
                startPoint.getX() + (Math.cos(positiveHeadingShift) * offsetDistance),
                startPoint.getY() + (Math.sin(positiveHeadingShift) * offsetDistance)
            ),
        };

        Coordinate[] centerLineCoordinates = new Coordinate[]{
            new Coordinate(startPoint.getX(), startPoint.getY()),
            new Coordinate(endPoint.getX(), endPoint.getY())
        };


        
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.polygon = factory.createPolygon(polyCoordinates);
        this.heading = headingFromNorth;
        this.laneWidth = laneWidthCm;
        this.centerLine = factory.createLineString(centerLineCoordinates);
        this.segmentID = segmentID;

    }

    public Polygon getPolygon() {
        return polygon;
    }

    public double getHeading() {
        return heading;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public double getLaneWidth() {
        return laneWidth;
    }

    public LineString getCenterLine() {
        return centerLine;
    }

    public int getSegmentID() {
        return segmentID;
    }
}
