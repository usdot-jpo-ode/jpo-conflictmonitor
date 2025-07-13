package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import us.dot.its.jpo.conflictmonitor.monitor.utils.CircleMath;

@Getter
@Setter
@ToString
@Slf4j
public class IntersectionLine {
    
    private double heading;
    private Point stopLinePoint;
    private Lane lane;

    public static IntersectionLine fromLane(Lane lane){
        log.debug("construct IntersectionLine from Lane: {}", lane);
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

    public IntersectionLine(Point stopLinePoint, double heading, Lane lane){
        log.debug("construct IntersectionLine from stopLinePoint: {}, heading: {}, Lane: {}", stopLinePoint, heading, lane);
        this.stopLinePoint = stopLinePoint;
        this.heading = heading;
        this.lane = lane;
    }


}
