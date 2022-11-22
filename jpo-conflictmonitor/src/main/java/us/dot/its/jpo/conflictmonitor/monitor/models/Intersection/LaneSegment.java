package us.dot.its.jpo.conflictmonitor.monitor.models.Intersection;

import org.locationtech.jts.geom.Polygon;

public class LaneSegment {
    private Polygon polygon;
    
    public LaneSegment(Polygon polygon, double heading) {
        this.polygon = polygon;
        this.heading = heading;
    }

    private double heading;

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public double getHeading() {
        return heading;
    }
    
    public void setHeading(double heading) {
        this.heading = heading;
    }
}
