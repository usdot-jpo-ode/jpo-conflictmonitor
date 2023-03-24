package us.dot.its.jpo.conflictmonitor.monitor.models;

import java.math.BigDecimal;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import us.dot.its.jpo.ode.plugin.j2735.J2735GenericLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeListXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeOffsetPointXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeSetXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735NodeXY;
import us.dot.its.jpo.ode.plugin.j2735.J2735Node_XY;
import us.dot.its.jpo.ode.plugin.j2735.OdePosition3D;

/**
 * Unit tests for {@link LaneConnection}
 */
public class LaneConnectionTest {
    
    @Test
    public void testDetectConflict_Parallel() {
        var connection = connection();
        var parallelConnection = parallelConnection();
        boolean result = connection.detectConflict(parallelConnection);
        assertThat("parallel connection", result, equalTo(false));
    }

    @Test
    public void testDetectConflict_Crossing() {
        var connection = connection();
        var crossingConnection = crossingConnection();
        boolean result = connection.detectConflict(crossingConnection);
        assertThat("crossing connection", result, equalTo(true));
    }



    public static final int SIGNAL_GROUP = 10;
    public static final OdePosition3D REFERENCE_POINT 
            = new OdePosition3D(BigDecimal.valueOf(39.7), BigDecimal.valueOf(-104.9), BigDecimal.valueOf(1609.0));
        

    public static LaneConnection connection() {
        var ingress = getGenericLane(new double[][] { { 2, 0 }, { 0, 1 } });
        var egress = getGenericLane(new double[][] { { 2, 3 }, { 0, 1 } });
        return new LaneConnection(REFERENCE_POINT, ingress, egress, SIGNAL_GROUP);

    }

    public static LaneConnection parallelConnection() {
        var ingress = getGenericLane(new double[][] { { 1, 0 }, { 0, 1 } });
        var egress = getGenericLane(new double[][] { { 1, 3 }, { 0, 1 } });
        return new LaneConnection(REFERENCE_POINT, ingress, egress, SIGNAL_GROUP);
    }

    public static LaneConnection crossingConnection() {
        var ingress = getGenericLane(new double[][] { { 0, 2 }, { 1, 0 } });
        var egress = getGenericLane(new double[][] { { 3, 2 }, { 1, 0 } });
        return new LaneConnection(REFERENCE_POINT, ingress, egress, SIGNAL_GROUP);
    }

   

    public static J2735GenericLane getGenericLane(double[][] offsets) {
        var lane = new J2735GenericLane();
        var nodeList = new J2735NodeListXY();
        lane.setNodeList(nodeList);
        var nodeSet = new J2735NodeSetXY();
        nodeList.setNodes(nodeSet);
        for (double[] point : offsets) {
            var node = new J2735NodeXY();
            var delta = new J2735NodeOffsetPointXY();
            var nodeXY = new J2735Node_XY();
            nodeXY.setX(BigDecimal.valueOf(point[0]));
            nodeXY.setY(BigDecimal.valueOf(point[1]));
            delta.setNodeXY1(nodeXY);
            node.setDelta(delta);
            nodeSet.getNodes().add(node);
        }
        return lane;
    }
}
