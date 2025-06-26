package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;

/**
 * Represents a segment of a lane within an intersection.
 * A LaneSegment is defined by its start and end points, width, heading, and polygonal area.
 */
public class LaneSegment {
    /** The polygon representing the area of the lane segment. */
    private final Polygon polygon;
    /** The starting point of the lane segment. */
    private final Point startPoint;
    /** The ending point of the lane segment. */
    private final Point endPoint;
    /** The center line of the lane segment as a LineString. */
    private final LineString centerLine;
    /** The heading of the lane segment in degrees from north. */
    private final double heading;
    /** The width of the lane segment in centimeters. */
    private final double laneWidth;
    /** The unique identifier for this lane segment. */
    private final int segmentID;

    /**
     * Constructs a LaneSegment with the specified parameters.
     *
     * @param startPoint   the starting Point of the segment
     * @param endPoint     the ending Point of the segment
     * @param laneWidthCm  the width of the lane in centimeters
     * @param ingress      true if the segment is for an ingress lane, false otherwise
     * @param segmentID    the unique identifier for this segment
     * @param factory      the GeometryFactory used to create geometric objects
     */
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

    /**
     * Gets the polygon representing the area of the lane segment.
     *
     * @return the polygon of the lane segment
     */
    public Polygon getPolygon() {
        return polygon;
    }

    /**
     * Gets the heading of the lane segment in degrees from north.
     *
     * @return the heading in degrees
     */
    public double getHeading() {
        return heading;
    }

    /**
     * Gets the starting point of the lane segment.
     *
     * @return the start Point
     */
    public Point getStartPoint() {
        return startPoint;
    }

    /**
     * Gets the ending point of the lane segment.
     *
     * @return the end Point
     */
    public Point getEndPoint() {
        return endPoint;
    }

    /**
     * Gets the width of the lane segment in centimeters.
     *
     * @return the lane width in centimeters
     */
    public double getLaneWidth() {
        return laneWidth;
    }

    /**
     * Gets the center line of the lane segment as a LineString.
     *
     * @return the center LineString
     */
    public LineString getCenterLine() {
        return centerLine;
    }

    /**
     * Gets the unique identifier for this lane segment.
     *
     * @return the segment ID
     */
    public int getSegmentID() {
        return segmentID;
    }
}
