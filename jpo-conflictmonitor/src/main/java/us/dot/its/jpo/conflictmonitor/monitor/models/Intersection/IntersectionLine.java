package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;
/**
 * IntersectionLines represent the start / endpoint of a lane where it connects to the main body of the intersection.
 */
@Getter
@Setter
public class IntersectionLine {
    
    /**
     * double representing the heading in degrees from north where the lane connects to the intersection
     */
    private double heading;

    /**
     * Point representing the individual coordinate where the stop intersection line is located
     */
    private Point stopLinePoint;

    /**
     * Lane object representing the lane that connects to the intersection line point
     */
    private Lane lane;

    /**
     * 
     * @param lane
     * @return IntersectionLine returns an instance of a IntersectionLine based calculated from the lane geometry.
     */
    public static IntersectionLine fromLane(Lane lane){
        LineString points = lane.getPoints();

        if(points.getNumPoints() >=2){
            Point startPoint = points.getStartPoint(); // Stop Line Point
            Point headingPoint = points.getPointN(1); // Secondary Point used to compute heading
            double heading = CircleMath.headingXYToHeadingFromNorth(Math.toDegrees(Math.atan2(startPoint.getY() - headingPoint.getY(), startPoint.getX() - headingPoint.getX())));
            
            return new IntersectionLine(startPoint, heading, lane);
        }else{
            return null;
        }
        
    }

    /**
     * Default Constructor for IntersectionLine
     * @param stopLinePoint
     * @param heading
     * @param lane
     */
    public IntersectionLine(Point stopLinePoint, double heading, Lane lane){
        this.stopLinePoint = stopLinePoint;
        this.heading = heading;
        this.lane = lane;
    }

    @Override
    public String toString(){
        return "StopLine - StopLinePoint: (" + this.stopLinePoint.getX() + "," + this.stopLinePoint.getY() + ") Heading: " + this.heading + " deg";
    }
}
