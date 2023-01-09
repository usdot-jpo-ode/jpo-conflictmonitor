package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;

public class IntersectionLine {
    
    private double heading;
    private Point centerPoint;
    private Lane lane;

    public static IntersectionLine fromLane(Lane lane){
        LineString points = lane.getPoints();

        if(points.getNumPoints() >=2){
            Point startPoint = points.getStartPoint(); // Stop Line Point
            Point headingPoint = points.getPointN(1); // Secondary Point used to compute heading
            double heading = CircleMath.headingXYToHeadingFromNorth(Math.toDegrees(Math.atan2(startPoint.getY() - headingPoint.getY(), startPoint.getX() - headingPoint.getX())));
            
            return new IntersectionLine(startPoint, heading, lane);
        }else{
            System.out.println("Unable to Create Intersection Line. Lane has less than 2 points.");
            return null;
        }
        
    }

    public IntersectionLine(Point centerPoint, double heading, Lane lane){
        this.centerPoint = centerPoint;
        this.heading = heading;
        this.lane = lane;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Point centerPoint) {
        this.centerPoint = centerPoint;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public Lane getLane() {
        return lane;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
    }

    @Override
    public String toString(){
        return "StopLine - Centerpoint: (" + this.centerPoint.getX() + "," + this.centerPoint.getY() + ") Heading: " + this.heading + " deg";
    }
}
