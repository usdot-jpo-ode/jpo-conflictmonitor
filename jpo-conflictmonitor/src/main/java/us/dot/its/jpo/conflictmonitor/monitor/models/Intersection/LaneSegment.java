package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;

public class LaneSegment {
    private Polygon polygon;
    private Point startPoint;
    private Point endPoint;
    private LineString centerLine;
    private double heading;
    private double laneWidth;

    public LaneSegment(Point startPoint, Point endPoint, double laneWidthCm, boolean ingress, GeometryFactory factory) {
        double headingRadians = 0;
        if(ingress){
            headingRadians = Math.atan2(startPoint.getY() - endPoint.getY(),startPoint.getX() - endPoint.getX());
        }else{
            headingRadians = Math.atan2(endPoint.getY() - startPoint.getY(),endPoint.getX() - startPoint.getX()); 
        }
        
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
}
