package us.dot.its.jpo.conflictmonitor.monitor.models.intersection;

import org.junit.Test;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import us.dot.its.jpo.conflictmonitor.monitor.models.Intersection.LaneSegment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LaneSegmentTest {

    @Test
    public void testConstructor() {
        var factory = new GeometryFactory();
        final Point startPoint = factory.createPoint(new CoordinateXY(1.0, 1.0));
        final Point endPoint = factory.createPoint(new CoordinateXY(3.0, 1.0));
        final double laneWidthCm = 1000.0;
        final boolean ingress = false;

        var segment = new LaneSegment(startPoint, endPoint, laneWidthCm, ingress, factory);

        assertThat("Start Point", segment.getStartPoint(), equalTo(startPoint));
        assertThat("End Point", segment.getEndPoint(), equalTo(endPoint));
        assertThat("Polygon not null", segment.getPolygon(), notNullValue());
        assertThat("Number of polygon coordinates", segment.getPolygon().getCoordinates(), arrayWithSize(5));
        assertThat("Heading", segment.getHeading(), closeTo(270.0, 0.1));
        assertThat("Center line", segment.getCenterLine(), notNullValue());
        assertThat("Center line coordinates", segment.getCenterLine().getCoordinates(), arrayWithSize(2));
        assertThat("Lane width", segment.getLaneWidth(), equalTo(laneWidthCm));
    }
}
