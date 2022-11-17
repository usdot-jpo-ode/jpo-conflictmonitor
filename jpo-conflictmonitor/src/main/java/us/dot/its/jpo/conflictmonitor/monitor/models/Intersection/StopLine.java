package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

public class StopLine {
    
    private double heading;
    private Point centerPoint;

    public static StopLine fromIngressLane(Lane ingressLane){
        LineString points = ingressLane.getPoints();

        if(points.getNumPoints() >=2){
            Point startPoint = points.getStartPoint(); // Stop Line Point
            Point headingPoint = points.getPointN(1); // Secondary Point used to compute heading
            double heading = Math.toDegrees(Math.atan2(startPoint.getY() - headingPoint.getY(), startPoint.getX() - headingPoint.getX()));

            return new StopLine(startPoint, heading);
        }else{
            System.out.println("Unable to Create Ingress Lane. Lane has less than 2 points.");
            return null;
        }
        
    }

    public StopLine(Point centerPoint, double heading){
        this.centerPoint = centerPoint;
        this.heading = heading;
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

    @Override
    public String toString(){
        return "StopLine - Centerpoint: (" + this.centerPoint.getX() + "," + this.centerPoint.getY() + ") Heading: " + this.heading + " deg";
    }
}
