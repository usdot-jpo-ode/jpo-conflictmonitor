package us.dot.its.jpo.conflictmonitor.monitor.models;

import org.junit.Test;

import static us.dot.its.jpo.conflictmonitor.monitor.models.LaneTestUtil.*;
import us.dot.its.jpo.ode.plugin.j2735.J2735IntersectionGeometry;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for {@link MapIntersection}
 */
public class MapIntersectionTest {

    final int SIGNAL_GROUP = 10;
    final OdePosition3D refPoint = getRefPoint(-104.9, 39.7, 1609.0);

    @Test
    public void testConstructor() {
         // Signal group with all ingress lanes connected to all egresses.
        
        var ingress1 = getGenericLane(1, new double[][] { { 2, 0 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[]{2, 4, 6});
        var egress1 = getGenericLane(2, new double[][] { { 2, 3 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[]{1, 3, 5});
        var ingress2 = getGenericLane(3, new double[][] { { 1, 0 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[]{2, 4, 6});
        var egress2 = getGenericLane(4, new double[][] { { 1, 3 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[]{1, 3, 5});
        var ingress3 = getGenericLane(5, new double[][] { { 0, 2 }, { 1, 0 } }, SIGNAL_GROUP, new Integer[]{2, 4, 6});
        var egress3 = getGenericLane(6, new double[][] { { 3, 2 }, { 1, 0 } }, SIGNAL_GROUP, new Integer[]{1, 3, 5});
        
        J2735IntersectionGeometry intersectionGeom = intersectionGeometry(refPoint, ingress1, egress1, ingress2, egress2, ingress3, egress3);
        MapIntersection intersection = new MapIntersection(intersectionGeom);
        assertThat(intersection, notNullValue());
        assertThat(intersection.getLaneConnections(), hasSize(equalTo(18)));
    }

    @Test
    public void testGetConnectionsAsWKT() {
        var ingress1 = getGenericLane(1, new double[][] { { 2, 0 }, { 0, 1 } }, null, new Integer[]{ 2 });
        var egress1 = getGenericLane(2, new double[][] { { 2, 3 }, { 0, 1 } }, null, new Integer[] { 1 });
        J2735IntersectionGeometry intersectionGeom = intersectionGeometry(refPoint, ingress1, egress1);
        MapIntersection intersection = new MapIntersection(intersectionGeom);
        String wkt = intersection.getConnectionsAsWKT();
        assertThat(wkt, notNullValue());
        assertThat(wkt, containsString("LINESTRING"));
    }

    @Test
    public void testNullConnectsTo() {
        // Signal group with egress lanes with null connects to
        var ingress1 = getGenericLane(1, new double[][] { { 2, 0 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[]{2});
        var egress1 = getGenericLane(2, new double[][] { { 2, 3 }, { 0, 1 } }, SIGNAL_GROUP, null);
        J2735IntersectionGeometry intersectionGeom = intersectionGeometry(refPoint, ingress1, egress1);
        MapIntersection intersection = new MapIntersection(intersectionGeom);
        assertThat(intersection, notNullValue());
        assertThat(intersection.getLaneConnections(), hasSize(equalTo(1)));
    }

    @Test
    public void testNullConnectingLaneId() {           
        var ingress1 = getGenericLane(1, new double[][] { { 2, 0 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[]{ null });
        var egress1 = getGenericLane(2, new double[][] { { 2, 3 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[] { null });
        J2735IntersectionGeometry intersectionGeom = intersectionGeometry(refPoint, ingress1, egress1);
        MapIntersection intersection = new MapIntersection(intersectionGeom);
        assertThat(intersection, notNullValue());
        assertThat(intersection.getLaneConnections(), hasSize(equalTo(0)));
    }

    @Test
    public void testNonexistentConnectingLaneIds() {
        var ingress1 = getGenericLane(1, new double[][] { { 2, 0 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[]{ 20 });
        var egress1 = getGenericLane(2, new double[][] { { 2, 3 }, { 0, 1 } }, SIGNAL_GROUP, new Integer[] { 30 });
        J2735IntersectionGeometry intersectionGeom = intersectionGeometry(refPoint, ingress1, egress1);
        MapIntersection intersection = new MapIntersection(intersectionGeom);
        assertThat(intersection, notNullValue());
        assertThat(intersection.getLaneConnections(), hasSize(equalTo(0)));
    }

    @Test
    public void testNullSignalGroup() {
        var ingress1 = getGenericLane(1, new double[][] { { 2, 0 }, { 0, 1 } }, null, new Integer[]{ 2 });
        var egress1 = getGenericLane(2, new double[][] { { 2, 3 }, { 0, 1 } }, null, new Integer[] { 1 });
        J2735IntersectionGeometry intersectionGeom = intersectionGeometry(refPoint, ingress1, egress1);
        MapIntersection intersection = new MapIntersection(intersectionGeom);
        assertThat(intersection, notNullValue());
        assertThat(intersection.getLaneConnections(), hasSize(equalTo(2)));
    }

    @Test
    public void testNullRefPoint() {
        var ingress1 = getGenericLane(1, new double[][] { { 2, 0 }, { 0, 1 } }, null, new Integer[]{ 2 });
        var egress1 = getGenericLane(2, new double[][] { { 2, 3 }, { 0, 1 } }, null, new Integer[] { 1 });
        J2735IntersectionGeometry intersectionGeom = intersectionGeometry(null, ingress1, egress1);
        MapIntersection intersection = new MapIntersection(intersectionGeom);
        assertThat(intersection, notNullValue());
        assertThat(intersection.getLaneConnections(), hasSize(equalTo(2)));
    }

    @Test
    public void testNullLaneId() {
        var ingress1 = getGenericLane(null, new double[][] { { 2, 0 }, { 0, 1 } }, null, new Integer[]{ null });
        var egress1 = getGenericLane(null, new double[][] { { 2, 3 }, { 0, 1 } }, null, new Integer[] { null });
        J2735IntersectionGeometry intersectionGeom = intersectionGeometry(refPoint, ingress1, egress1);
        MapIntersection intersection = new MapIntersection(intersectionGeom);
        assertThat(intersection, notNullValue());
        assertThat(intersection.getLaneConnections(), hasSize(equalTo(0)));
    }
}
