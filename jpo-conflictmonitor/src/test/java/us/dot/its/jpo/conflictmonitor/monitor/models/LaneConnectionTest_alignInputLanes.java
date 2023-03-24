package us.dot.its.jpo.conflictmonitor.monitor.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.LineString;

/**
 * Unit test for {@link LaneConnection#alignInputLanes()}
 */
@RunWith(Parameterized.class)
public class LaneConnectionTest_alignInputLanes {
    
    @Test
    public void testAlignInputLanes() {
        LaneConnection laneConnection = new LaneConnection();
        laneConnection.setIngressPath(ingressPath);
        laneConnection.setEgressPath(egressPath);
        laneConnection.alignInputLanes();
        assertThat(laneConnection.getIngressPath(), equalTo(expectedAlignedIngress));
        assertThat(laneConnection.getEgressPath(), equalTo(expectedAlignedEgress));
    }

    LineString ingressPath;
    LineString egressPath;
    LineString expectedAlignedIngress;
    LineString expectedAlignedEgress;

    public LaneConnectionTest_alignInputLanes(LineString ingressPath, LineString egressPath, 
        LineString expectedAlignedIngress, LineString expectedAlignedEgress) {
        this.ingressPath = ingressPath;
        this.egressPath = egressPath;
        this.expectedAlignedIngress = expectedAlignedIngress;
        this.expectedAlignedEgress = expectedAlignedEgress;
    }

    @Parameters
    public static Collection<Object[]> getParams() {
        return Arrays.asList(new Object[][]{
            { getLineString(0, 1), getLineString(3, 4), getLineString(0, 1), getLineString(3, 4) },
            { getLineString(1, 0), getLineString(3, 4), getLineString(0, 1), getLineString(3, 4) },
            { getLineString(0, 1), getLineString(4, 3), getLineString(0, 1), getLineString(3, 4) },
            { getLineString(1, 0), getLineString(4, 3), getLineString(0, 1), getLineString(3, 4) }
        });
    }

    private static LineString getLineString(double y1, double y2) {
        var x = 2;
        var coordinates = new Coordinate[] {new CoordinateXY(x, y1), new CoordinateXY(x, y2)};
        var geometryFactory = JTSFactoryFinder.getGeometryFactory();
        return geometryFactory.createLineString(coordinates);
    }
}
